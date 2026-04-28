package br.gov.caixa.caixaverso.backend.OrdersPackage.dto;

import br.gov.caixa.caixaverso.backend.OrdersPackage.domainmodel.OrderStatus;

import java.util.List;

public record PurchaseOrderCreateRequest(
        String customerName,
        OrderStatus status,
        List<OrderItemRequest> items
        ) {}
