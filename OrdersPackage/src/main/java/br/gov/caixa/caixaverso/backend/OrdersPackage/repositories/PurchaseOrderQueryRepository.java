package br.gov.caixa.caixaverso.backend.OrdersPackage.repositories;

import br.gov.caixa.caixaverso.backend.OrdersPackage.domainmodel.PurchaseOrder;
import br.gov.caixa.caixaverso.backend.OrdersPackage.service.PurchaseOrderSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PurchaseOrderQueryRepository {
    Page<PurchaseOrder> search(PurchaseOrderSearchCriteria criteria, Pageable pageable);
}
