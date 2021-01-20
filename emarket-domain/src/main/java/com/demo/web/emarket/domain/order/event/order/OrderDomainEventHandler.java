package com.demo.web.emarket.domain.order.event.order;

import com.demo.web.emarket.domain.ddd.DDD;
import com.demo.web.emarket.domain.ddd.event.DomainEventDispatcher;
import com.demo.web.emarket.domain.order.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@DDD.DomainService
@Service
public class OrderDomainEventHandler {

    @Autowired
    private OrderDomainEventProducer orderDomainEventProducer;
    @Autowired
    private DomainEventDispatcher domainEventDispatcher;

    public void generateOrderedSingleProductDomainEvent(Order order){
        OrderSingleProduct orderSingleProduct = orderDomainEventProducer.producedOrderSingleProduct(order);
        domainEventDispatcher.dispatch(orderSingleProduct, Order.class);
    }
}
