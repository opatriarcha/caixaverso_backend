package com.banking.batch.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "interest_calculations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterestCalculation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal principalAmount;

    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal interestRate;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal interestEarned;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal newBalance;

    @Column(nullable = false)
    private LocalDateTime calculatedAt;

    @Column
    private String period; // e.g. "2024-01" for monthly
}
