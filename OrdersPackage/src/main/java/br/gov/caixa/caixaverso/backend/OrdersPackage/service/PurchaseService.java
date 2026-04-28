package br.gov.caixa.caixaverso.backend.OrdersPackage.service;

import br.gov.caixa.caixaverso.backend.OrdersPackage.domainmodel.PurchaseOrder;
import br.gov.caixa.caixaverso.backend.OrdersPackage.mapper.OrderCommandMapper;
import br.gov.caixa.caixaverso.backend.OrdersPackage.repositories.PurchaseOrderQueryRepository;
import br.gov.caixa.caixaverso.backend.OrdersPackage.repositories.PurchaseOrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.nio.channels.FileChannel;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PurchaseService {

//    @Autowired
    private final PurchaseOrdersRepository purchaseOrdersRepository;
    private final OrderCommandMapper orderCommandMapper;
    private final PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer;
    private final PurchaseOrderQueryRepository purchaseOrderQueryRepository;


    public PurchaseOrder create(CreatePurchaseOrderCommand createPurchaseOrderCommand) {
        PurchaseOrder order = new PurchaseOrder();
        order.setCustomerName( createPurchaseOrderCommand.customerName());
        order.setStatus( createPurchaseOrderCommand.status());
        order.setCreatedAt(OffsetDateTime.now());
        order.replaceItems(createPurchaseOrderCommand.items().stream().map(orderCommandMapper::toEntity).toList());
        return this.purchaseOrdersRepository.save(order);
    }

    public Page<PurchaseOrder> search( PurchaseOrderSearchCriteria criteria, Pageable pageable){
        return this.purchaseOrderQueryRepository.search(criteria, pageable);
    }

    public Page<PurchaseOrder> findAll(Pageable pageable) {
        return this.purchaseOrdersRepository.findAll(pageable);
    }

    public Optional<PurchaseOrder> findById(Long id) {
        return this.purchaseOrdersRepository.findById(id);
    }
}
