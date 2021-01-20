package com.demo.web.emarket.domain.order.event.customer;

import com.demo.web.emarket.domain.ddd.event.DomainEvent;
import com.demo.web.emarket.domain.order.event.customer.model.CustomerModel;
import com.demo.web.emarket.domain.order.event.order.model.OrderModel;

import javax.validation.Payload;
import java.time.LocalDateTime;

public class CustomerAdded extends DomainEvent {

    private static final String EVENT_VERSION="1.0";
    private CustomerModel payload;

    public CustomerAdded(String idUtilisateur, CustomerModel payload) {
        super(EVENT_VERSION, LocalDateTime.now(),idUtilisateur, payload.id, payload.numVersionObjet, payload.getClass().getCanonicalName());
        this.payload = payload;
    }

    public CustomerModel getPayload() {
        return payload;
    }

    public CustomerAdded() {
        this(null, null);
    }
}
