package com.example.caixa_batch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
public class BatchJobController {

    private final JobLauncher jobLauncher;
    private final Job transactionProcessingJob;
    private final Job statementGenerationJob;
    private final Job interestCalculationJob;
    private final Job marketplaceOfferImportJob;

    @PostMapping("/transactions/run")
    public ResponseEntity<Map<String, Object>> runTransactionJob(
            @RequestParam(required = false) String inputFile) {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("run.id", System.currentTimeMillis())
                    .addString("input.file", inputFile != null ? inputFile : "")
                    .toJobParameters();

            var execution = jobLauncher.run(transactionProcessingJob, params);
            return ResponseEntity.ok(Map.of(
                    "jobName", "transactionProcessingJob",
                    "executionId", execution.getId(),
                    "status", execution.getStatus().toString()
            ));
        } catch (Exception e) {
            log.error("Failed to run transactionProcessingJob", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/statements/run")
    public ResponseEntity<Map<String, Object>> runStatementJob() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("run.id", System.currentTimeMillis())
                    .toJobParameters();

            var execution = jobLauncher.run(statementGenerationJob, params);
            return ResponseEntity.ok(Map.of(
                    "jobName", "statementGenerationJob",
                    "executionId", execution.getId(),
                    "status", execution.getStatus().toString()
            ));
        } catch (Exception e) {
            log.error("Failed to run statementGenerationJob", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/interest/run")
    public ResponseEntity<Map<String, Object>> runInterestJob() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("run.id", System.currentTimeMillis())
                    .toJobParameters();

            var execution = jobLauncher.run(interestCalculationJob, params);
            return ResponseEntity.ok(Map.of(
                    "jobName", "interestCalculationJob",
                    "executionId", execution.getId(),
                    "status", execution.getStatus().toString()
            ));
        } catch (Exception e) {
            log.error("Failed to run interestCalculationJob", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/batch/marketplace/run
     *
     * Dispara a importação de um CSV de ofertas do marketplace bancário.
     *
     * Sem parâmetro: usa o arquivo padrão em classpath (data/marketplace_offers.csv).
     * Com parâmetro: lê o caminho absoluto informado no sistema de arquivos.
     *
     * Exemplo:
     *   curl -X POST "http://localhost:8080/api/batch/marketplace/run"
     *   curl -X POST "http://localhost:8080/api/batch/marketplace/run?inputFile=/tmp/offers.csv"
     */
    @PostMapping("/marketplace/run")
    public ResponseEntity<Map<String, Object>> runMarketplaceImportJob(
            @RequestParam(required = false) String inputFile) {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("run.id", System.currentTimeMillis())
                    .addString("input.file", inputFile != null ? inputFile : "")
                    .toJobParameters();

            var execution = jobLauncher.run(marketplaceOfferImportJob, params);
            return ResponseEntity.ok(Map.of(
                    "jobName", "marketplaceOfferImportJob",
                    "executionId", execution.getId(),
                    "status", execution.getStatus().toString()
            ));
        } catch (Exception e) {
            log.error("Failed to run marketplaceOfferImportJob", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
