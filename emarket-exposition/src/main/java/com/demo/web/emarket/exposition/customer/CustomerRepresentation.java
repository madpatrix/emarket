package com.demo.web.emarket.exposition.customer;

import com.demo.web.emarket.domain.customer.Customer;
import com.demo.web.emarket.domain.customer.CustomerName;
import com.demo.web.emarket.domain.customer.NamePart;
import com.demo.web.emarket.exposition.product.ProductRepresentation;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomerRepresentation {

    @JsonProperty
    public String id;
    @JsonProperty public String firstName;
    @JsonProperty public String lastName;

    public Customer toDomain(){
        Customer customer = new Customer(new CustomerName(new NamePart(firstName), new NamePart(lastName)));
        return customer;
    }
}
