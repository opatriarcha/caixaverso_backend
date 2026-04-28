package com.example.caixa_batch.jobs.interest;

import com.banking.batch.model.Account;
import com.banking.batch.model.InterestCalculation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Feature 3 - Partitioned Step: Computes monthly interest per account.
 */
@Slf4j
@Component
public class InterestCalculationProcessor implements ItemProcessor<Account, InterestCalculation> {

    private static final BigDecimal MONTHS_IN_YEAR = BigDecimal.valueOf(12);

    @Override
    public InterestCalculation process(Account account) {
        // Only process SAVINGS accounts
        if (!"SAVINGS".equals(account.getAccountType())) {
            log.debug("Skipping non-savings account: {}", account.getAccountNumber());
            return null;
        }

        BigDecimal monthlyRate = account.getInterestRate().divide(MONTHS_IN_YEAR, 10, RoundingMode.HALF_UP);
        BigDecimal interestEarned = account.getBalance()
                .multiply(monthlyRate)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal newBalance = account.getBalance().add(interestEarned);
        String period = LocalDate.now().getYear() + "-" + String.format("%02d", LocalDate.now().getMonthValue());

        log.info("Interest for account {}: principal={}, rate={}, earned={}, newBalance={}",
                account.getAccountNumber(), account.getBalance(), account.getInterestRate(),
                interestEarned, newBalance);

        return InterestCalculation.builder()
                .accountNumber(account.getAccountNumber())
                .principalAmount(account.getBalance())
                .interestRate(account.getInterestRate())
                .interestEarned(interestEarned)
                .newBalance(newBalance)
                .calculatedAt(LocalDateTime.now())
                .period(period)
                .build();
    }
}
