package br.gov.caixa.caixaverso.backend.OrdersPackage.mapper;

import br.gov.caixa.caixaverso.backend.OrdersPackage.domainmodel.OrderItem;
import br.gov.caixa.caixaverso.backend.OrdersPackage.service.OrderItemCommand;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderCommandMapper {
    OrderItem toEntity(OrderItemCommand command);
}
