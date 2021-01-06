package com.demo.web.emarket.exposition.order;

import com.demo.web.emarket.exposition.product.ProductRepresentation;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderRepresentation {

    @JsonProperty public String id;
    @JsonProperty public ProductRepresentation product;
    @JsonProperty public int quantity;
}
