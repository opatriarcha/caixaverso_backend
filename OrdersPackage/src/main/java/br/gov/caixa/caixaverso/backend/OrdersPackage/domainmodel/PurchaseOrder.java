package br.gov.caixa.caixaverso.backend.OrdersPackage.domainmodel;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchase_orders")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Getter
    @Setter Long id;

    @Column(nullable = false, length = 120)
    private @Getter
    @Setter String customerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private @Getter
    @Setter OrderStatus status;

    @Column(nullable = true)
    private @Getter
    @Setter OffsetDateTime createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("id ASC")
    private @Getter
    @Setter List<OrderItem> items;

    public BigDecimal getTotalAmount() {
        return items.stream().map(OrderItem::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void replaceItems(List<OrderItem> newItems) {
        this.items.clear();
        if (newItems != null) {
            newItems.forEach(this::addItem);
        }
    }

    private void addItem(OrderItem orderItem) {
        this.items.add(orderItem);
    }
}

