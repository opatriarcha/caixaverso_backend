package com.company.bank.repository;

import com.company.bank.domainmodel.Branch;
import org.hibernate.SessionFactory;

public class BranchRepository extends SimpleRepository<Branch, Long> {
    public BranchRepository(SessionFactory sf) {
        super(sf, Branch.class);
    }
}
