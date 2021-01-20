package com.demo.web.emarket.domain.order.event.order;

import com.demo.web.emarket.domain.ddd.event.DomainEvent;
import com.demo.web.emarket.domain.order.event.order.model.OrderModel;

import java.time.LocalDateTime;

public class OrderSingleProduct extends DomainEvent {

    private static final String EVENT_VERSION="1.0";
    private OrderModel orderModel;

    public OrderSingleProduct(OrderModel orderModel) {
        super(EVENT_VERSION, LocalDateTime.now());
        this.orderModel = orderModel;
    }
}
