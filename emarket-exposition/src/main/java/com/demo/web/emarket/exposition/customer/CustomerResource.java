package com.demo.web.emarket.exposition.customer;

import com.demo.web.emarket.application.customer.AddCustomer;
import com.demo.web.emarket.application.order.OrderProduct;
import com.demo.web.emarket.domain.UniqueId;
import com.demo.web.emarket.exposition.order.OrderRepresentation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/customer", produces = MediaType.APPLICATION_JSON_VALUE)
public class CustomerResource {

    private AddCustomer addCustomer;

    public CustomerResource(AddCustomer addCustomer) {
        this.addCustomer = addCustomer;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addCustomer(@RequestBody CustomerRepresentation representation){
        //UniqueId uniqueId = this.addCustomer.addCustomer(representation.toDomain());
        for(int i=0; i<10000; i+=1000){
            this.addCustomer.addCustomer(representation.toDomain(), i);
            System.out.println("ADDED "+ i + " no of customer number!");
        }
        return ResponseEntity.ok("ok");
    }
}
