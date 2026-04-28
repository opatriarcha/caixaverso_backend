package br.gov.caixa.caixaverso.backend.OrdersPackage.repositories;

import br.gov.caixa.caixaverso.backend.OrdersPackage.domainmodel.OrderItem;
import br.gov.caixa.caixaverso.backend.OrdersPackage.domainmodel.PurchaseOrder;
import br.gov.caixa.caixaverso.backend.OrdersPackage.domainmodel.QOrderItem;
import br.gov.caixa.caixaverso.backend.OrdersPackage.domainmodel.QPurchaseOrder;
import br.gov.caixa.caixaverso.backend.OrdersPackage.service.PurchaseOrderSearchCriteria;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PurchaseOrderQueryRepositoryImpl implements PurchaseOrderQueryRepository{



    @PersistenceContext
    private EntityManager entitymanager;

    private final JPAQueryFactory queryFactory = new JPAQueryFactory(entitymanager);

    @Override
    public Page<PurchaseOrder> search(PurchaseOrderSearchCriteria criteria, Pageable pageable) {
        QPurchaseOrder order = QPurchaseOrder.purchaseOrder;
        QOrderItem item = QOrderItem.orderItem;
        BooleanBuilder where = new BooleanBuilder();

        if (criteria.customerName() != null && !criteria.customerName().isBlank()) {
            where.and(order.customerName.containsIgnoreCase(criteria.customerName()));
        }
        if (criteria.status() != null) {
            where.and(order.status.eq(criteria.status()));
        }

        NumberExpression<BigDecimal> totalExpr = Expressions.numberTemplate(BigDecimal.class, "sum({0} * {1})", item.unitPrice, item.quantity);

        JPAQuery<Long> idQuery = queryFactory.select(order.id)
                .from(order)
                .leftJoin(order.items, item)
                .where(where)
                .groupBy(order.id)
                .having(criteria.minTotal() == null ? null : totalExpr.goe(criteria.minTotal()));

        applySort(idQuery, pageable.getSort(), order);
        List<Long> ids = idQuery.offset(pageable.getOffset()).limit(pageable.getPageSize()).fetch();

        long total = queryFactory.select(order.id)
                .from(order)
                .leftJoin(order.items, item)
                .where(where)
                .groupBy(order.id)
                .having(criteria.minTotal() == null ? null : totalExpr.goe(criteria.minTotal()))
                .fetch().size();

        if (ids.isEmpty()) {
            return Page.empty(pageable);
        }

        List<PurchaseOrder> content = queryFactory.selectDistinct(order)
                .from(order)
                .leftJoin(order.items, item).fetchJoin()
                .where(order.id.in(ids))
                .fetch();

        content.sort((a, b) -> ids.indexOf(a.getId()) - ids.indexOf(b.getId()));
        content.forEach(po -> po.getItems().sort((OrderItem a, OrderItem b) -> a.getId().compareTo(b.getId())));
        return new PageImpl<>(content, pageable, total);
    }

    private void applySort(JPAQuery<Long> query, Sort sort, QPurchaseOrder order) {
        List<OrderSpecifier<?>> specifiers = new ArrayList<>();
        if (sort.isUnsorted()) {
            specifiers.add(order.id.asc());
        } else {
            for (Sort.Order current : sort) {
                Order direction = current.isAscending() ? Order.ASC : Order.DESC;
                switch (current.getProperty()) {
                    case "id" -> specifiers.add(new OrderSpecifier<>(direction, order.id));
                    case "customerName" -> specifiers.add(new OrderSpecifier<>(direction, order.customerName));
                    case "status" -> specifiers.add(new OrderSpecifier<>(direction, order.status));
                    case "createdAt" -> specifiers.add(new OrderSpecifier<>(direction, order.createdAt));
                    default -> specifiers.add(new OrderSpecifier<>(direction, order.id));
                }
            }
        }
        query.orderBy(specifiers.toArray(OrderSpecifier[]::new));
    }






}
