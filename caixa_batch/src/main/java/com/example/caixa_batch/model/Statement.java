package com.example.caixa_batch.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "statements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Statement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String ownerName;

    @Column(nullable = false)
    private LocalDate statementDate;

    @Column(nullable = false)
    private LocalDate periodStart;

    @Column(nullable = false)
    private LocalDate periodEnd;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal openingBalance;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal closingBalance;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal totalCredits;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal totalDebits;

    @Column(nullable = false)
    private Integer transactionCount;

    @Column
    private LocalDateTime generatedAt;

    @Column
    private String filePath;
}
