package com.demo.web.emarket.domain.order;

import com.demo.web.emarket.domain.UniqueId;
import com.demo.web.emarket.domain.ddd.DDD;

import java.util.Set;

@DDD.DomainRepository
public interface OrdersPort {
    Order getOrThrow(UniqueId orderId);
    Order add(Order order);
    Set<Order> getAll();
}
