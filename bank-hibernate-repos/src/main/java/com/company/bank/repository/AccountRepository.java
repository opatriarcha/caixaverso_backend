package com.company.bank.repository;

import com.company.bank.domainmodel.Account;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.List;

public class AccountRepository extends SimpleRepository<Account, Long> {
    public AccountRepository(SessionFactory sf) {
        super(sf, Account.class);
    }

    public List<Account> findByCustomerId(Long customerId) {
        try (Session session = sf.openSession()) {
            return session.createQuery(
                            "select a from Account a where a.customer.id = :cid",
                            Account.class
                    )
                    .setParameter("cid", customerId)
                    .getResultList();
        }
    }
}
