// src/test/java/com/example/bank/repo/BranchEmployeeRepositoryTest.java
package com.company.bank.repository;

import com.company.bank.config.HibernateUtil;
import com.company.bank.domainmodel.Branch;
import com.company.bank.domainmodel.Employee;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class BranchEmployeeRepositoryTest {

    private static SessionFactory sf;
    private static BranchRepository branchRepo;
    private static EmployeeRepository employeeRepo;

    @BeforeAll
    static void setup() {
        sf = HibernateUtil.newSessionFactory();
        branchRepo = new BranchRepository(sf);
        employeeRepo = new EmployeeRepository(sf);
    }

    @AfterAll
    static void tearDown() {
        sf.close();
    }

//    @Test
//    void shouldPersistManyToMany_BranchEmployees() {
//        Employee e1 = new Employee("Carla");
//        Employee e2 = new Employee("Diego");
//
//        Branch b1 = new Branch("BR-001");
//        Branch b2 = new Branch("BR-002");
//
//        b1.addEmployee(e1);
//        b1.addEmployee(e2);
//        b2.addEmployee(e2);
//
//        Long b1Id = branchRepo.save(b1);
//        Long b2Id = branchRepo.save(b2);
//
//        assertNotNull(b1Id);
//        assertNotNull(b2Id);
//
//        // Valida N:N dentro de transação (carrega coleções lazy com segurança)
//        branchRepo.inTx(session -> {
//            Branch loadedB1 = session.find(Branch.class, b1Id);
//            Branch loadedB2 = session.find(Branch.class, b2Id);
//
//            assertNotNull(loadedB1);
//            assertNotNull(loadedB2);
//
//            assertEquals(2, loadedB1.getEmployees().size());
//            assertEquals(1, loadedB2.getEmployees().size());
//
//            // Toca no lado inverso para garantir que está consistente
//            Employee diego = loadedB1.getEmployees().stream()
//                    .filter(e -> e.getName().equals("Diego"))
//                    .findFirst()
//                    .orElseThrow();
//
//            assertTrue(diego.getBranches().size() >= 1);
//
//            return null;
//        });
//    }

//    @Test
//    void shouldPersistManyToMany_BranchEmployees1() {
//        Employee e1 = new Employee("Carla");
//        Employee e2 = new Employee("Diego");
//
//        Branch b1 = new Branch("BR-001");
//        Branch b2 = new Branch("BR-002");
//
//        b1.addEmployee(e1);
//        b1.addEmployee(e2);
//        b2.addEmployee(e2);
//
//        Long b1Id = branchRepo.save(b1);
//        Long b2Id = branchRepo.save(b2);
//
//        assertNotNull(b1Id);
//        assertNotNull(b2Id);
//
//        branchRepo.inTx(session -> {
//            Branch loadedB1 = session.createQuery(
//                    "select b from Branch b join fetch b.employees where b.id = :id",
//                    Branch.class
//            ).setParameter("id", b1Id).getSingleResult();
//
//            Branch loadedB2 = session.createQuery(
//                    "select b from Branch b join fetch b.employees where b.id = :id",
//                    Branch.class
//            ).setParameter("id", b2Id).getSingleResult();
//
//            assertEquals(2, loadedB1.getEmployees().size());
//            assertEquals(1, loadedB2.getEmployees().size());
//            return null;
//        });
//    }

    @Test
    void shouldPersistManyToMany_BranchEmployees() {

        // 1) salva os employees (ciclo de vida independente)
        Employee e1 = new Employee("Carla");
        Employee e2 = new Employee("Diego");

        Long e1Id = employeeRepo.save(e1);
        Long e2Id = employeeRepo.save(e2);

        // 2) numa transação, recarrega managed e cria os branches
        Long[] ids = branchRepo.inTx(session -> {

            Employee me1 = session.find(Employee.class, e1Id);
            Employee me2 = session.find(Employee.class, e2Id);

            Branch b1 = new Branch("BR-001");
            Branch b2 = new Branch("BR-002");

            b1.addEmployee(me1);
            b1.addEmployee(me2);
            b2.addEmployee(me2);

            session.persist(b1);
            session.persist(b2);
            session.flush();

            return new Long[] {
                    (Long) session.getIdentifier(b1),
                    (Long) session.getIdentifier(b2)
            };
        });

        Long b1Id = ids[0];
        Long b2Id = ids[1];

        // 3) valida com join fetch
        branchRepo.inTx(session -> {
            Branch loadedB1 = session.createQuery(
                    "select b from Branch b join fetch b.employees where b.id = :id",
                    Branch.class
            ).setParameter("id", b1Id).getSingleResult();

            Branch loadedB2 = session.createQuery(
                    "select b from Branch b join fetch b.employees where b.id = :id",
                    Branch.class
            ).setParameter("id", b2Id).getSingleResult();

            assertEquals(2, loadedB1.getEmployees().size());
            assertEquals(1, loadedB2.getEmployees().size());

            return null;
        });
    }
}