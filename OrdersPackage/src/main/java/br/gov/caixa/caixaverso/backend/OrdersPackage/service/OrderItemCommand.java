package br.gov.caixa.caixaverso.backend.OrdersPackage.service;

import java.math.BigDecimal;

public record OrderItemCommand(
        String productName,
        Integer quantity,
        BigDecimal unitPrice
) {
}
