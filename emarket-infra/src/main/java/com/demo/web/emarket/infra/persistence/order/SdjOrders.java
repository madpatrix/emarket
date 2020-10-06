package com.demo.web.emarket.infra.persistence.order;

import com.demo.web.emarket.domain.UniqueId;
import com.demo.web.emarket.domain.ddd.DDD;
import com.demo.web.emarket.domain.order.Order;
import com.demo.web.emarket.domain.order.Orders;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
@DDD.DomainRepositoryImpl
public class SdjOrders implements Orders {
    private final OrdersJpaRepo jpaRepo;

    public SdjOrders(OrdersJpaRepo jpaRepo) {
        this.jpaRepo = jpaRepo;
    }

    @Override
    public Order getOrThrow(UniqueId orderId) {
        return jpaRepo.getOne(orderId);
    }

    @Override
    public Order add(Order order) {
        return jpaRepo.saveAndFlush(order);
    }

    @Override
    public Set<Order> getAll() {
        return new HashSet<>(jpaRepo.findAll());
    }
}
