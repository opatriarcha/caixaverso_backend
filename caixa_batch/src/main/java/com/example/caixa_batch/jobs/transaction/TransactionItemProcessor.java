package com.example.caixa_batch.jobs.transaction;

import com.banking.batch.model.Transaction;
import com.example.caixa_batch.model.TransactionCsvRecord;
import com.example.caixa_batch.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Feature 1 - ItemProcessor: Validates business rules and transforms CSV records to entities.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionItemProcessor implements ItemProcessor<TransactionCsvRecord, Transaction> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final AccountRepository accountRepository;

    @Override
    public Transaction process(TransactionCsvRecord record) {
        log.debug("Processing transaction for account: {}", record.getAccountNumber());

        // Validate account exists
        if (accountRepository.findByAccountNumber(record.getAccountNumber()).isEmpty()) {
            log.warn("Account {} not found, skipping transaction", record.getAccountNumber());
            return null; // returning null skips the item (Spring Batch feature)
        }

        // Validate transaction type
        if (!record.getTransactionType().matches("CREDIT|DEBIT")) {
            log.warn("Invalid transaction type: {}, skipping", record.getTransactionType());
            return null;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(record.getAmount());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("Invalid amount {}, skipping", amount);
                return null;
            }
        } catch (NumberFormatException e) {
            log.error("Cannot parse amount '{}', skipping", record.getAmount());
            return null;
        }

        return Transaction.builder()
                .accountNumber(record.getAccountNumber())
                .transactionType(record.getTransactionType())
                .amount(amount)
                .description(record.getDescription())
                .transactionDate(LocalDateTime.parse(record.getTransactionDate(), FORMATTER))
                .status("PROCESSED")
                .processedAt(LocalDateTime.now())
                .build();
    }
}
