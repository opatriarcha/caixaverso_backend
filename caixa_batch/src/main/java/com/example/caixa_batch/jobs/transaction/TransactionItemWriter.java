package com.example.caixa_batch.jobs.transaction;

import com.example.caixa_batch.model.Account;
import com.example.caixa_batch.model.Transaction;
import com.example.caixa_batch.repository.AccountRepository;
import com.example.caixa_batch.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Feature 1 - ItemWriter: Saves transactions and updates account balances.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionItemWriter implements ItemWriter<Transaction> {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public void write(Chunk<? extends Transaction> chunk) {
        for (Transaction transaction : chunk.getItems()) {
            transactionRepository.save(transaction);

            Optional<Account> accountOpt = accountRepository.findByAccountNumber(transaction.getAccountNumber());
            accountOpt.ifPresent(account -> {
                if ("CREDIT".equals(transaction.getTransactionType())) {
                    account.setBalance(account.getBalance().add(transaction.getAmount()));
                } else {
                    BigDecimal newBalance = account.getBalance().subtract(transaction.getAmount());
                    account.setBalance(newBalance.max(BigDecimal.ZERO));
                }
                accountRepository.save(account);
                log.debug("Updated balance for account {}: {}", account.getAccountNumber(), account.getBalance());
            });
        }
        log.info("Written {} transactions to database", chunk.size());
    }
}
