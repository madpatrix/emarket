package com.demo.web.emarket.infra.persistence.order;

import com.demo.web.emarket.domain.UniqueId;
import com.demo.web.emarket.domain.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersJpaRepo extends JpaRepository<Order, UniqueId> {
}
