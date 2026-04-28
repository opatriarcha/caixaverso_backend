package br.gov.caixa.caixaverso.backend.OrdersPackage.domainmodel;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


@Entity
@Table(name = "order_items")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private @Getter @Setter Long id;

    @Column(nullable = false, length = 120)
    private @Getter @Setter String productName;

    @Column(nullable = false)
    private @Getter @Setter Integer quantity;

    @Column(nullable = false, precision = 15, scale = 2)
    private @Getter @Setter BigDecimal unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private @Getter @Setter PurchaseOrder order;

    public BigDecimal getSubtotal() { return unitPrice.multiply(BigDecimal.valueOf(quantity)); }
}