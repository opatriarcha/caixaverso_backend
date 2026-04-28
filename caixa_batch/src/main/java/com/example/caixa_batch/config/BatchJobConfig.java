package com.example.caixa_batch.config;


import com.banking.batch.model.InterestCalculation;

import com.example.caixa_batch.jobs.interest.AccountRangePartitioner;
import com.example.caixa_batch.jobs.interest.InterestCalculationProcessor;
import com.example.caixa_batch.jobs.interest.InterestCalculationWriter;
import com.example.caixa_batch.jobs.statement.StatementItemProcessor;
import com.example.caixa_batch.jobs.statement.StatementItemWriter;
import com.example.caixa_batch.jobs.transaction.TransactionItemProcessor;
import com.example.caixa_batch.jobs.transaction.TransactionItemReader;
import com.example.caixa_batch.jobs.transaction.TransactionItemWriter;
import com.example.caixa_batch.listener.BankingJobExecutionListener;
import com.example.caixa_batch.model.Account;
import com.example.caixa_batch.model.Statement;
import com.example.caixa_batch.model.Transaction;
import com.example.caixa_batch.model.TransactionCsvRecord;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class BatchJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final BankingJobExecutionListener jobListener;



    // ─────────────────────────────────────────────────────────────────
    // JOB 1: Transaction Processing (CSV → DB)
    // Feature: FlatFileItemReader + ItemProcessor with null-filter (skip)
    // ─────────────────────────────────────────────────────────────────

    @Bean
    public Job transactionProcessingJob(
            TransactionItemReader readerFactory,
            TransactionItemProcessor processor,
            TransactionItemWriter writer) {

        Step step = new StepBuilder("transactionStep", jobRepository)
                .<TransactionCsvRecord, Transaction>chunk(50, transactionManager)
                .reader(transactionFileReader(readerFactory, null))
                .processor((ItemProcessor<? super TransactionCsvRecord, ? extends Transaction>) processor)
                .writer((ItemWriter<? super Transaction>) writer)
                .faultTolerant()
                .skipLimit(10)
                .skip(Exception.class)
                .build();

        return new JobBuilder("transactionProcessingJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(jobListener)
                .start(step)
                .build();
    }

    @Bean
    @StepScope
    public FlatFileItemReader<TransactionCsvRecord> transactionFileReader(
            TransactionItemReader factory,
            @Value("#{jobParameters['input.file'] ?: null}") String filePath) {

        Resource resource = (filePath != null)
                ? new FileSystemResource(filePath)
                : new ClassPathResource("data/transactions.csv");

        return factory.reader(resource);
    }

    // ─────────────────────────────────────────────────────────────────
    // JOB 2: Statement Generation
    // Feature: JpaCursorItemReader (streaming large result sets efficiently)
    //          + CompositeItemWriter pattern
    // ─────────────────────────────────────────────────────────────────

    @Bean
    public Job statementGenerationJob(
            StatementItemProcessor processor,
            StatementItemWriter writer) {

        Step step = new StepBuilder("statementStep", jobRepository)
                .<Account, Statement>chunk(10, transactionManager)
                .reader(accountCursorReader())
                .processor(processor)
                .writer(writer)
                .build();

        return new JobBuilder("statementGenerationJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(jobListener)
                .start(step)
                .build();
    }

    @Bean
    public JpaCursorItemReader<Account> accountCursorReader() {
        return new JpaCursorItemReaderBuilder<Account>()
                .name("accountCursorReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT a FROM Account a ORDER BY a.accountNumber")
                .build();
    }

    // ─────────────────────────────────────────────────────────────────
    // JOB 3: Interest Calculation
    // Feature: Partitioned Step — splits savings accounts across threads
    // ─────────────────────────────────────────────────────────────────

    @Bean
    public Job interestCalculationJob(
            AccountRangePartitioner partitioner,
            InterestCalculationProcessor processor,
            InterestCalculationWriter writer) {

        Step workerStep = new StepBuilder("interestWorkerStep", jobRepository)
                .<Account, InterestCalculation>chunk(5, transactionManager)
                .reader(partitionedAccountReader(Long.MIN_VALUE, Long.MAX_VALUE))
                .processor((ItemProcessor<? super Account, ? extends InterestCalculation>) processor)
                .writer(writer)
                .build();

        TaskExecutorPartitionHandler partitionHandler = new TaskExecutorPartitionHandler();
        partitionHandler.setStep(workerStep);
        partitionHandler.setGridSize(4);
        partitionHandler.setTaskExecutor(new SimpleAsyncTaskExecutor("interest-partition-"));

        Step managerStep = new StepBuilder("interestManagerStep", jobRepository)
                .partitioner("interestWorkerStep", partitioner)
                .partitionHandler(partitionHandler)
                .build();

        return new JobBuilder("interestCalculationJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(jobListener)
                .start(managerStep)
                .build();
    }

    @Bean
    @StepScope
    public JpaCursorItemReader<Account> partitionedAccountReader(
            @Value("#{stepExecutionContext['minId'] ?: 0L}") Long minId,
            @Value("#{stepExecutionContext['maxId'] ?: 9999999L}") Long maxId) {

        return new JpaCursorItemReaderBuilder<Account>()
                .name("partitionedAccountReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("SELECT a FROM Account a WHERE a.accountType = 'SAVINGS' AND a.id BETWEEN :minId AND :maxId")
                .parameterValues(Map.of("minId", minId, "maxId", maxId))
                .build();
    }

    // JpaItemWriter for InterestCalculation (used as a secondary writer option demo)
    @Bean
    public JpaItemWriter<InterestCalculation> interestJpaWriter() {
        return new JpaItemWriterBuilder<InterestCalculation>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────
    // JOB 4: Marketplace Offer Import (CSV → DB)
    // Feature: importa ofertas de produtos bancários de parceiros via CSV
    //          com validação de negócio e idempotência por offer_id
    // ─────────────────────────────────────────────────────────────────

//    @Bean
//    public Job marketplaceOfferImportJob(
//            MarketplaceOfferItemReader readerFactory,
//            MarketplaceOfferItemProcessor processor,
//            MarketplaceOfferItemWriter writer) {
//
//        Step step = new StepBuilder("marketplaceOfferStep", jobRepository)
//                .<MarketplaceOfferCsvRecord, MarketplaceOffer>chunk(100, transactionManager)
//                .reader(marketplaceOfferFileReader(readerFactory, null))
//                .processor(processor)
//                .writer(writer)
//                .faultTolerant()
//                .skipLimit(50)
//                .skip(Exception.class)
//                .build();
//
//        return new JobBuilder("marketplaceOfferImportJob", jobRepository)
//                .incrementer(new RunIdIncrementer())
//                .listener(jobListener)
//                .start(step)
//                .build();
//    }

//    @Bean
//    @StepScope
//    public FlatFileItemReader<MarketplaceOfferCsvRecord> marketplaceOfferFileReader(
//            MarketplaceOfferItemReader factory,
//            @Value("#{jobParameters['input.file']}") String filePath) {
//
//        Resource resource = (filePath != null && !filePath.isBlank())
//                ? new FileSystemResource(filePath)
//                : new ClassPathResource("data/marketplace_offers.csv");
//
//        return factory.reader(resource);
//    }
}
