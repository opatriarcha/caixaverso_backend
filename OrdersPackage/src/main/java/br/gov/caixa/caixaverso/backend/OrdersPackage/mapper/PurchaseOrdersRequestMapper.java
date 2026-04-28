package br.gov.caixa.caixaverso.backend.OrdersPackage.mapper;

import br.gov.caixa.caixaverso.backend.OrdersPackage.dto.PurchaseOrderCreateRequest;
import br.gov.caixa.caixaverso.backend.OrdersPackage.service.CreatePurchaseOrderCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PurchaseOrdersRequestMapper {
    CreatePurchaseOrderCommand toCreateCommand(PurchaseOrderCreateRequest request);
//    UpdatePurchaseOrderCommand toUpdateCommand(PurchaseOrderUpdateRequest request);
//    OrderItemCommand toCommand(OrderItemRequest request);
}
