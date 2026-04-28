package com.example.caixa_batch.jobs.statement;

import com.banking.batch.model.Account;
import com.banking.batch.model.Statement;
import com.example.caixa_batch.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Feature 2 - StatementProcessor: Aggregates transaction data per account to generate statement.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StatementItemProcessor implements ItemProcessor<Account, Statement> {

    private final TransactionRepository transactionRepository;

    @Override
    public Statement process(Account account) {
        LocalDate today = LocalDate.now();
        LocalDate periodStart = today.withDayOfMonth(1);
        LocalDate periodEnd = today;

        LocalDateTime startDt = periodStart.atStartOfDay();
        LocalDateTime endDt = periodEnd.atTime(23, 59, 59);

        BigDecimal totalCredits = transactionRepository
                .sumCreditsByAccountAndPeriod(account.getAccountNumber(), startDt, endDt);
        BigDecimal totalDebits = transactionRepository
                .sumDebitsByAccountAndPeriod(account.getAccountNumber(), startDt, endDt);
        Integer txCount = transactionRepository
                .countByAccountAndPeriod(account.getAccountNumber(), startDt, endDt);

        BigDecimal openingBalance = account.getBalance()
                .subtract(totalCredits)
                .add(totalDebits);

        log.info("Generating statement for account {} - period: {} to {}", account.getAccountNumber(), periodStart, periodEnd);

        return Statement.builder()
                .accountNumber(account.getAccountNumber())
                .ownerName(account.getOwnerName())
                .statementDate(today)
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .openingBalance(openingBalance.max(BigDecimal.ZERO))
                .closingBalance(account.getBalance())
                .totalCredits(totalCredits)
                .totalDebits(totalDebits)
                .transactionCount(txCount != null ? txCount : 0)
                .generatedAt(LocalDateTime.now())
                .filePath("statements/" + account.getAccountNumber() + "_" + today + ".pdf")
                .build();
    }
}
