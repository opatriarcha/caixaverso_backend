package com.company.bank.repository;

import com.company.bank.domainmodel.Customer;
import org.hibernate.SessionFactory;

public class CustomerRepository extends SimpleRepository<Customer, Long> {
    public CustomerRepository(SessionFactory sf) {
        super(sf, Customer.class);
    }
}
