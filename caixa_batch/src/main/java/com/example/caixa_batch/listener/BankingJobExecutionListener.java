package com.example.caixa_batch.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class BankingJobExecutionListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("========================================");
        log.info("Starting Job: {} | Run ID: {}",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getId());
        log.info("Parameters: {}", jobExecution.getJobParameters());
        log.info("========================================");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        long durationMs = Duration.between(
                jobExecution.getStartTime(),
                jobExecution.getEndTime()
        ).toMillis();

        log.info("========================================");
        log.info("Finished Job: {} | Status: {} | Duration: {}ms",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStatus(),
                durationMs);
        jobExecution.getStepExecutions().forEach(step ->
                log.info("  Step [{}] read={} write={} skip={} status={}",
                        step.getStepName(),
                        step.getReadCount(),
                        step.getWriteCount(),
                        step.getSkipCount(),
                        step.getStatus()));
        log.info("========================================");
    }
}
