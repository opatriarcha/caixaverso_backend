package br.gov.caixa.caixaverso.backend.OrdersPackage.dto;

import br.gov.caixa.caixaverso.backend.OrdersPackage.domainmodel.OrderStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record PurchaseOrderResponse(
   Long id,
   String customerName,
   OrderStatus status,
   OffsetDateTime createdAt,
   List<OrderItemResponse> items,
   BigDecimal totalAmount,
   String shippingMessage
) {}
