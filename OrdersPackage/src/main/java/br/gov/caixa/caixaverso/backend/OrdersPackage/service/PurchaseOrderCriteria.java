package br.gov.caixa.caixaverso.backend.OrdersPackage.service;

import br.gov.caixa.caixaverso.backend.OrdersPackage.domainmodel.OrderStatus;

import java.math.BigDecimal;

public record PurchaseOrderCriteria(
        String customerName,
        OrderStatus status,
        BigDecimal minTotal
) {
}
