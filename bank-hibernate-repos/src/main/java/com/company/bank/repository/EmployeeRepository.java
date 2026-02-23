package com.company.bank.repository;

import com.company.bank.domainmodel.Employee;
import org.hibernate.SessionFactory;

public class EmployeeRepository extends SimpleRepository<Employee, Long> {
    public EmployeeRepository(SessionFactory sf) {
        super(sf, Employee.class);
    }
}
