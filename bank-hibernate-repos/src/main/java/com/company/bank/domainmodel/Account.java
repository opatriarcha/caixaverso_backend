package com.company.bank.domainmodel;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String number;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BankTransaction> transactions = new ArrayList<>();

    protected Account() {}

    public Account(String number) {
        this.number = number;
    }

    void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void addTransaction(BankTransaction tx) {
        transactions.add(tx);
        tx.setAccount(this);
    }

    public Long getId() { return id; }
    public String getNumber() { return number; }
    public Customer getCustomer() { return customer; }
    public List<BankTransaction> getTransactions() { return transactions; }
}
