package com.company.bank.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.function.Function;

public class SimpleRepository<T, ID> {

    protected final SessionFactory sf;
    private final Class<T> entityClass;

    public SimpleRepository(SessionFactory sf, Class<T> entityClass) {
        this.sf = sf;
        this.entityClass = entityClass;
    }

//    public ID save(T entity) {
//        return inTx(session -> (ID) session.merge(entity));
//    }
//    s/ 🔥 Agora salva corretamente e retorna o ID
    @SuppressWarnings("unchecked")
    public ID save(T entity) {
        return inTx(session -> {
            session.persist(entity);
            session.flush(); // garante geração do ID
            return (ID) session.getIdentifier(entity);
        });
    }

    public void delete(T entity) {
        inTx(session -> { session.remove(entity); return null; });
    }

    public T findById(ID id) {
        try (Session session = sf.openSession()) {
            return session.find(entityClass, id);
        }
    }

    public List<T> findAll() {
        try (Session session = sf.openSession()) {
            return session.createQuery("from " + entityClass.getSimpleName(), entityClass).getResultList();
        }
    }

//    public <R> R inTx(Function<Session, R> work) {
//        Transaction tx = null;
//        try (Session session = sf.openSession()) {
//            tx = session.beginTransaction();
//            R r = work.apply(session);
//            tx.commit();
//            return r;
//        } catch (RuntimeException e) {
//            if (tx != null) tx.rollback();
//            throw e;
//        }
//    }

    public <R> R inTx(Function<Session, R> work) {
        Transaction tx = null;
        try (Session session = sf.openSession()) {
            tx = session.beginTransaction();
            R result = work.apply(session);

            if (tx.isActive()) {
                tx.commit();
            }
            return result;

        } catch (RuntimeException e) {
            // Se a transação ainda estiver ativa, tenta rollback.
            // Se já foi marcada/fechada pelo Hibernate/JDBC, não força.
            try {
                if (tx != null && tx.isActive()) {
                    tx.rollback();
                }
            } catch (RuntimeException ignored) {
                // Evita mascarar a exceção original
            }
            throw e; // mantém a exceção real
        }
    }





}
