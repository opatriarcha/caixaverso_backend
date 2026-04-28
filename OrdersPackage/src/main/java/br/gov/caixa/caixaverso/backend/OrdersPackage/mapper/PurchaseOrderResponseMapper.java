package br.gov.caixa.caixaverso.backend.OrdersPackage.mapper;

import br.gov.caixa.caixaverso.backend.OrdersPackage.domainmodel.OrderItem;
import br.gov.caixa.caixaverso.backend.OrdersPackage.domainmodel.PurchaseOrder;
import br.gov.caixa.caixaverso.backend.OrdersPackage.dto.OrderItemResponse;
import br.gov.caixa.caixaverso.backend.OrdersPackage.dto.PurchaseOrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
@Mapper(componentModel = "spring")
public interface PurchaseOrderResponseMapper {

    @Mapping(target = "totalAmount", expression = "java(entity.getTotalAmount())")
    @Mapping(target = "shippingMessage", ignore = true)
    PurchaseOrderResponse toResponse(PurchaseOrder entity);

    @Mapping(target = "subtotal", expression = "java(entity.getSubtotal())")
    OrderItemResponse toResponse(OrderItem entity);

//    default PurchaseOrderResponse toResponse(PurchaseOrder entity, ShipmentQuoteResponse shipment) {
//        PurchaseOrderResponse response = toResponse(entity);
//        return new PurchaseOrderResponse(
//                response.id(),
//                response.customerName(),
//                response.status(),
//                response.createdAt(),
//                response.items(),
//                response.totalAmount(),
//                shipment == null ? null : shipment.message()
//        );
//    }
}
