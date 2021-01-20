package com.demo.web.emarket.application.order;

import com.demo.web.emarket.application.ApplicationService;
import com.demo.web.emarket.domain.UniqueId;
import com.demo.web.emarket.domain.ddd.DDD;
import com.demo.web.emarket.domain.order.*;
import com.demo.web.emarket.domain.order.event.order.OrderDomainEventHandler;

import static java.util.Arrays.asList;

@DDD.ApplicationService
@ApplicationService
public class OrderProduct {
    private final OrdersPort ordersPort;
    private final OrderDomainEventHandler orderDomainEventHandler;

    public OrderProduct(OrdersPort ordersPort, OrderDomainEventHandler orderDomainEventHandler) {
        this.ordersPort = ordersPort;
        this.orderDomainEventHandler = orderDomainEventHandler;
    }

    public UniqueId orderProduct(OrderSingleProductCommand orderSingleProductCommand) {
        Line line = new Line(orderSingleProductCommand.getQuantity(), orderSingleProductCommand.getProduct().getId());
        Order newOrder = new Order(asList(line), OrderStatus.INITIATED, orderSingleProductCommand.getCustomerId());
        this.orderDomainEventHandler.generateOrderedSingleProductDomainEvent(newOrder);
        return ordersPort.add(newOrder).getId();
    }



    public Order getOrder(UniqueId id){
        return ordersPort.getOrThrow(id);
    }
}
