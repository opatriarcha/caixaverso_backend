package com.example.caixa_batch.jobs.statement;

import com.banking.batch.model.Account;
import com.banking.batch.model.Statement;
import com.example.caixa_batch.repository.AccountRepository;
import com.example.caixa_batch.repository.StatementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Feature 2 - CompositeItemWriter pattern:
 * Persists the Statement AND updates lastStatementDate on Account in one writer.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StatementItemWriter implements ItemWriter<Statement> {

    private final StatementRepository statementRepository;
    private final AccountRepository accountRepository;

    @Override
    public void write(Chunk<? extends Statement> chunk) {
        for (Statement statement : chunk.getItems()) {
            // Persist the statement
            statementRepository.save(statement);

            // Update account's last statement date
            accountRepository.findByAccountNumber(statement.getAccountNumber())
                    .ifPresent(account -> {
                        account.setLastStatementDate(LocalDate.now());
                        accountRepository.save(account);
                    });

            log.info("Statement saved for account {} | Credits: {} | Debits: {} | Closing Balance: {}",
                    statement.getAccountNumber(),
                    statement.getTotalCredits(),
                    statement.getTotalDebits(),
                    statement.getClosingBalance());
        }
    }
}
