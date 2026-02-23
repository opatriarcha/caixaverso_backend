package com.company.bank.domainmodel;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Address address;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts = new ArrayList<>();

    protected Customer() {}

    public Customer(String fullName) {
        this.fullName = fullName;
    }

    public void setAddress(Address address) {
        this.address = address;
        address.setCustomer(this);
    }

    public void addAccount(Account account) {
        accounts.add(account);
        account.setCustomer(this);
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public Address getAddress() { return address; }
    public List<Account> getAccounts() { return accounts; }
}
