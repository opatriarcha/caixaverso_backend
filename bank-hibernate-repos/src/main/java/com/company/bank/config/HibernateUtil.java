// src/main/java/com/example/bank/config/HibernateUtil.java
package com.company.bank.config;

import com.company.bank.domainmodel.*;
import com.example.bank.domainmodel.*;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.Map;

public final class HibernateUtil {

    private HibernateUtil() {}

    public static SessionFactory newSessionFactory() {

        StandardServiceRegistry registry =
                new StandardServiceRegistryBuilder()
                        .applySettings(Map.of(
                                "hibernate.connection.driver_class", "org.h2.Driver",
                                "hibernate.connection.url", "jdbc:h2:mem:bank;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
                                "hibernate.connection.username", "sa",
                                "hibernate.connection.password", "",
                                "hibernate.hbm2ddl.auto", "create-drop",
                                "hibernate.show_sql", "true",
                                "hibernate.format_sql", "true",
                                "hibernate.use_sql_comments", "true",
                                "hibernate.jdbc.time_zone", "UTC"
                        ))
                        .build();

        return new MetadataSources(registry)
                .addAnnotatedClass(Customer.class)
                .addAnnotatedClass(Address.class)
                .addAnnotatedClass(Account.class)
                .addAnnotatedClass(BankTransaction.class)
                .addAnnotatedClass(Branch.class)
                .addAnnotatedClass(Employee.class)
                .buildMetadata()
                .buildSessionFactory();
    }
}