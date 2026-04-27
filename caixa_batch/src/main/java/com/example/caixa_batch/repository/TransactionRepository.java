package com.example.caixa_batch.repository;

import com.banking.batch.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByStatus(String status);

    List<Transaction> findByAccountNumber(String accountNumber);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.accountNumber = :accountNumber AND t.transactionType = 'CREDIT' AND t.transactionDate BETWEEN :start AND :end AND t.status = 'PROCESSED'")
    BigDecimal sumCreditsByAccountAndPeriod(String accountNumber, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.accountNumber = :accountNumber AND t.transactionType = 'DEBIT' AND t.transactionDate BETWEEN :start AND :end AND t.status = 'PROCESSED'")
    BigDecimal sumDebitsByAccountAndPeriod(String accountNumber, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.accountNumber = :accountNumber AND t.transactionDate BETWEEN :start AND :end AND t.status = 'PROCESSED'")
    Integer countByAccountAndPeriod(String accountNumber, LocalDateTime start, LocalDateTime end);
}
