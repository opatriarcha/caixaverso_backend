package br.gov.caixa.caixaverso.backend.OrdersPackage.dto;

import java.math.BigDecimal;

public record OrderItemRequest(
        String productName,
        Integer quantity,
        BigDecimal unitPrice
) {
}
