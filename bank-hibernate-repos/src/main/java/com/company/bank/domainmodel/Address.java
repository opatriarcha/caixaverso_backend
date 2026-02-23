package com.company.bank.domainmodel;

import jakarta.persistence.*;

@Entity
@Table(name = "addresses")
public class Address {

    @Id
    private Long customerId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(nullable = false)
    private String street;

    @Column(nullable = false)
    private String city;

    protected Address() {}

    public Address(String street, String city) {
        this.street = street;
        this.city = city;
    }

    void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Long getCustomerId() { return customerId; }
    public String getStreet() { return street; }
    public String getCity() { return city; }
}
