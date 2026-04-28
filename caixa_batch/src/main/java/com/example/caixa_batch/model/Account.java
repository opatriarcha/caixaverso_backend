package com.example.caixa_batch.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String ownerName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String accountType; // SAVINGS, CHECKING

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal balance;

    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal interestRate; // e.g. 0.0650 = 6.5% per year

    @Column
    private LocalDate lastStatementDate;

    @Column
    private LocalDateTime lastInterestCalculation;

    @Column
    private LocalDateTime createdAt;
}
