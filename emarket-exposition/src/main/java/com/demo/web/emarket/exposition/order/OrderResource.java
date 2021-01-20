package com.demo.web.emarket.exposition.order;

import com.demo.web.emarket.application.order.OrderProduct;
import com.demo.web.emarket.domain.UniqueId;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/order", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderResource {

    private OrderProduct orderProduct;

    public OrderResource(OrderProduct orderProduct) {
        this.orderProduct = orderProduct;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createOrder(@RequestBody OrderRepresentation representation){

        UniqueId uniqueId = this.orderProduct.orderProduct(representation.toCommand());
        return ResponseEntity.ok(uniqueId.getValue());
    }
}
