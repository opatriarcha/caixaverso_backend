// src/test/java/com/example/bank/repo/CustomerRepositoryTest.java
package com.company.bank.repository;

import com.company.bank.config.HibernateUtil;
import com.company.bank.domainmodel.Account;
import com.company.bank.domainmodel.Address;
import com.company.bank.domainmodel.BankTransaction;
import com.company.bank.domainmodel.Customer;
import com.example.bank.domainmodel.*;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CustomerRepositoryTest {

    private static SessionFactory sf;
    private static CustomerRepository customerRepo;

    @BeforeAll
    static void setup() {
        sf = HibernateUtil.newSessionFactory();
        customerRepo = new CustomerRepository(sf);
    }

    @AfterAll
    static void tearDown() {
        sf.close();
    }

    @Test
    void shouldPersistCustomerWithAddressAndAccounts_1to1_1toN() {
        Customer c = new Customer("Ana Martins");
        c.setAddress(new Address("Rua A, 123", "São Paulo"));

        Account a1 = new Account("ACC-001");
        Account a2 = new Account("ACC-002");

        a1.addTransaction(new BankTransaction(new BigDecimal("100.00")));
        a2.addTransaction(new BankTransaction(new BigDecimal("250.00")));

        c.addAccount(a1);
        c.addAccount(a2);

        Long id = customerRepo.save(c);
        assertNotNull(id);

        // Valida associações dentro de transação para não cair em LazyInitializationException
        customerRepo.inTx(session -> {
            Customer loaded = session.find(Customer.class, id);
            assertNotNull(loaded);
            assertEquals("Ana Martins", loaded.getFullName());

            assertNotNull(loaded.getAddress());
            assertEquals("São Paulo", loaded.getAddress().getCity());

            assertEquals(2, loaded.getAccounts().size());
            long txCount = loaded.getAccounts().stream().mapToLong(a -> a.getTransactions().size()).sum();
            assertEquals(2, txCount);

            return null;
        });
    }
}