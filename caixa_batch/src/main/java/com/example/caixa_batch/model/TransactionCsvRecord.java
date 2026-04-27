package com.example.caixa_batch.model;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransactionCsvRecord {

    @CsvBindByName(column = "account_number")
    private String accountNumber;

    @CsvBindByName(column = "transaction_type")
    private String transactionType;

    @CsvBindByName(column = "amount")
    private String amount;

    @CsvBindByName(column = "description")
    private String description;

    @CsvBindByName(column = "transaction_date")
    private String transactionDate;
}
