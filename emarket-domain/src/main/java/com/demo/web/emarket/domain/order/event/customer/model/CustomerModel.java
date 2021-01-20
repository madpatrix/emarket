package com.demo.web.emarket.domain.order.event.customer.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CustomerModel {
    @JsonProperty
    public String firstName;
    @JsonProperty
    public String lastName;
}
