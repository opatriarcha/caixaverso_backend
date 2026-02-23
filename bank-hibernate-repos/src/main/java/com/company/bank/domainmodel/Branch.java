package com.company.bank.domainmodel;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "branches")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

//    @ManyToMany
//    @JoinTable(
//            name = "branch_employees",
//            joinColumns = @JoinColumn(name = "branch_id"),
//            inverseJoinColumns = @JoinColumn(name = "employee_id")
//    )

//    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
//    @JoinTable(
//            name = "branch_employees",
//            joinColumns = @JoinColumn(name = "branch_id"),
//            inverseJoinColumns = @JoinColumn(name = "employee_id")
//    )
//    private Set<Employee> employees = new HashSet<>();

    @ManyToMany(cascade = { CascadeType.MERGE })
    @JoinTable(
            name = "branch_employees",
            joinColumns = @JoinColumn(name = "branch_id"),
            inverseJoinColumns = @JoinColumn(name = "employee_id")
    )
    private Set<Employee> employees = new HashSet<>();

    protected Branch() {}

    public Branch(String code) {
        this.code = code;
    }

    public void addEmployee(Employee e) {
        employees.add(e);
        e.getBranches().add(this);
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public Set<Employee> getEmployees() { return employees; }
}
