// src/test/java/com/example/bank/repo/AccountRepositoryTest.java
package com.company.bank.repository;

import com.company.bank.config.HibernateUtil;
import com.company.bank.domainmodel.Account;
import com.company.bank.domainmodel.Customer;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountRepositoryTest {

    private static SessionFactory sf;
    private static CustomerRepository customerRepo;
    private static AccountRepository accountRepo;

    @BeforeAll
    static void setup() {
        sf = HibernateUtil.newSessionFactory();
        customerRepo = new CustomerRepository(sf);
        accountRepo = new AccountRepository(sf);
    }

    @AfterAll
    static void tearDown() {
        sf.close();
    }

    @Test
    void shouldFindAccountsByCustomerId_Nto1_query() {
        Customer c = new Customer("Bruno Lima");
        c.addAccount(new Account("ACC-101"));
        c.addAccount(new Account("ACC-102"));

        Long customerId = customerRepo.save(c);
        assertNotNull(customerId);

        List<Account> accounts = accountRepo.findByCustomerId(customerId);
        assertEquals(2, accounts.size());

        // Valida N:1 (Account -> Customer) dentro de transação
        accountRepo.inTx(session -> {
            for (Account a : accounts) {
                Account managed = session.find(Account.class, a.getId());
                assertNotNull(managed);
                assertEquals(customerId, managed.getCustomer().getId());
            }
            return null;
        });
    }
}