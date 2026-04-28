package com.example.caixa_batch.jobs.transaction;

import com.example.caixa_batch.model.TransactionCsvRecord;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * Feature 1 - FlatFileItemReader: Reads transactions from CSV file.
 */
@Component
public class TransactionItemReader {

    public FlatFileItemReader<TransactionCsvRecord> reader(Resource resource) {
        BeanWrapperFieldSetMapper<TransactionCsvRecord> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(TransactionCsvRecord.class);

        return new FlatFileItemReaderBuilder<TransactionCsvRecord>()
                .name("transactionItemReader")
                .resource(resource)
                .delimited()
                .delimiter(",")
                .names("account_number", "transaction_type", "amount", "description", "transaction_date")
                .fieldSetMapper(fieldSetMapper)
                .linesToSkip(1)
                .build();
    }
}
