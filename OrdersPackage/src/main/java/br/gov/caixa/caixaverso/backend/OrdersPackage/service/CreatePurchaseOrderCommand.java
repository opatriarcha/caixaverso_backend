package br.gov.caixa.caixaverso.backend.OrdersPackage.service;

import br.gov.caixa.caixaverso.backend.OrdersPackage.domainmodel.OrderStatus;

import java.util.List;

public record CreatePurchaseOrderCommand(
        String customerName,
        OrderStatus status,
        List<OrderItemCommand> items
) {
}
