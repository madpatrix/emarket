package com.demo.web.emarket.application.order;

import com.demo.web.emarket.application.ApplicationService;
import com.demo.web.emarket.domain.UniqueId;
import com.demo.web.emarket.domain.ddd.DDD;
import com.demo.web.emarket.domain.order.*;

import static java.util.Arrays.asList;

@DDD.ApplicationService
@ApplicationService
public class OrderProduct {
    private final OrdersPort ordersPort;

    public OrderProduct(OrdersPort ordersPort) {
        this.ordersPort = ordersPort;
    }

    public UniqueId orderProduct(OrderSingleProductCommand orderSingleProductCommand) {
        Line line = new Line(orderSingleProductCommand.getQuantity(), orderSingleProductCommand.getProduct().getId());
        Order newOrder = new Order(asList(line), OrderStatus.INITIATED, orderSingleProductCommand.getCustomerId());
        return ordersPort.add(newOrder).getId();
    }



    public Order getOrder(UniqueId id){
        return ordersPort.getOrThrow(id);
    }
}
