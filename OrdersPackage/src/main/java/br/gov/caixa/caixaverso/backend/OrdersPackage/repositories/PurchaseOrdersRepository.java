package br.gov.caixa.caixaverso.backend.OrdersPackage.repositories;

import br.gov.caixa.caixaverso.backend.OrdersPackage.domainmodel.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseOrdersRepository extends JpaRepository<PurchaseOrder, Long> {

}
