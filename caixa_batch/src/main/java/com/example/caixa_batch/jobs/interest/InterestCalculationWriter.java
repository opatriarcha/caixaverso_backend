package com.example.caixa_batch.jobs.interest;

import com.banking.batch.model.InterestCalculation;
import com.example.caixa_batch.repository.AccountRepository;
import com.example.caixa_batch.repository.InterestCalculationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class InterestCalculationWriter implements ItemWriter<InterestCalculation> {

    private final InterestCalculationRepository interestCalculationRepository;
    private final AccountRepository accountRepository;

    @Override
    public void write(Chunk<? extends InterestCalculation> chunk) {
        for (InterestCalculation calc : chunk.getItems()) {
            interestCalculationRepository.save(calc);

            accountRepository.findByAccountNumber(calc.getAccountNumber())
                    .ifPresent(account -> {
                        account.setBalance(calc.getNewBalance());
                        account.setLastInterestCalculation(LocalDateTime.now());
                        accountRepository.save(account);
                    });

            log.info("Saved interest calculation for account {}: earned {}",
                    calc.getAccountNumber(), calc.getInterestEarned());
        }
    }
}
