package com.demo.web.emarket.domain.order.event.order;

import com.demo.web.emarket.domain.ddd.DDD;
import com.demo.web.emarket.domain.order.Order;
import com.demo.web.emarket.domain.order.event.order.model.OrderModel;
import org.springframework.stereotype.Service;

@DDD.DomainService
@Service
public class OrderDomainEventProducer {

    public OrderSingleProduct producedOrderSingleProduct(Order order){
        OrderModel orderModel = new OrderModel();
        orderModel.customerId = order.getCustomerId().getValue();
        orderModel.product = order.orderLines().get(0).productId().getValue();
        orderModel.quantity = order.orderLines().size();

        return new OrderSingleProduct(orderModel);
    }
}
