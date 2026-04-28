package br.gov.caixa.caixaverso.backend.OrdersPackage.controller;

import br.gov.caixa.caixaverso.backend.OrdersPackage.domainmodel.PurchaseOrder;
import br.gov.caixa.caixaverso.backend.OrdersPackage.dto.PurchaseOrderCreateRequest;
import br.gov.caixa.caixaverso.backend.OrdersPackage.dto.PurchaseOrderResponse;
import br.gov.caixa.caixaverso.backend.OrdersPackage.mapper.PurchaseOrderResponseMapper;
import br.gov.caixa.caixaverso.backend.OrdersPackage.mapper.PurchaseOrdersRequestMapper;
import br.gov.caixa.caixaverso.backend.OrdersPackage.service.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


//"http://localhost:8080/api/orders"

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseService purchaseService;
    private final PurchaseOrdersRequestMapper purchaseOrdersRequestMapper;
    private final PurchaseOrderResponseMapper purchaseOrderResponseMapper;


    @GetMapping
    @RequestMapping("/hello") //http://localhost:8080/api/orders/hello
    public String helloWord(){
        System.out.println("OLA SPRING FRAMEWORK ( Boot())");
        return "Ola Mundo";
    }

    @PostMapping//http://localhost:8080/api/orders
    public ResponseEntity<PurchaseOrder> create( @RequestBody PurchaseOrderCreateRequest purchaseOrderCreateRequest){
        PurchaseOrder newOrder = this.purchaseService.create(purchaseOrdersRequestMapper.toCreateCommand(purchaseOrderCreateRequest));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(newOrder.getId())
                .toUri();
//        return ResponseEntity.ok(newOrder);
        return ResponseEntity.created(location).body(newOrder);
    }

    @GetMapping
    public ResponseEntity<Page<PurchaseOrderResponse>> findAll(@PageableDefault(page=0, size=10, sort = "id") Pageable pageable){
        return ResponseEntity.ok( purchaseService.findAll(pageable).map(purchaseOrderResponseMapper::toResponse));
    }

    @GetMapping("/{id}")//http://localhost:8080/api/orders/1
    public ResponseEntity<PurchaseOrderResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(
                purchaseOrderResponseMapper.toResponse(
                        this.purchaseService.findById(id)
                                .orElse(null)
                )
        );

    }


}

