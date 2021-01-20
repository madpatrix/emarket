package com.demo.web.emarket.exposition.order;

import com.demo.web.emarket.application.order.OrderSingleProductCommand;
import com.demo.web.emarket.domain.UniqueId;
import com.demo.web.emarket.domain.order.Quantity;
import com.demo.web.emarket.domain.product.Price;
import com.demo.web.emarket.domain.product.Product;
import com.demo.web.emarket.exposition.product.ProductRepresentation;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderRepresentation {

    @JsonProperty public String id;
    @JsonProperty public ProductRepresentation product;
    @JsonProperty public int quantity;


    public OrderSingleProductCommand toCommand(){
       return new OrderSingleProductCommand(
                new UniqueId(),
                new Product(new Price("30")),
                new Quantity(quantity)
        );
    }
}
