package com.demo.web.emarket.domain.order.event.customer;

import com.demo.web.emarket.domain.ddd.event.DomainEvent;
import com.demo.web.emarket.domain.order.event.customer.model.CustomerModel;
import com.demo.web.emarket.domain.order.event.order.model.OrderModel;

import java.time.LocalDateTime;

public class CustomerAdded extends DomainEvent {

    private static final String EVENT_VERSION="1.0";
    private CustomerModel customerModel;

    public CustomerAdded(CustomerModel customerModel) {
        super(EVENT_VERSION, LocalDateTime.now());
        this.customerModel = customerModel;
    }

    public CustomerModel getCustomerModel() {
        return customerModel;
    }

    public CustomerAdded() {
        this(null);
    }
}
