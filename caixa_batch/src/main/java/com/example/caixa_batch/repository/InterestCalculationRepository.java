package com.example.caixa_batch.repository;

import com.banking.batch.model.InterestCalculation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterestCalculationRepository extends JpaRepository<InterestCalculation, Long> {

    List<InterestCalculation> findByAccountNumber(String accountNumber);

    List<InterestCalculation> findByPeriod(String period);
}
