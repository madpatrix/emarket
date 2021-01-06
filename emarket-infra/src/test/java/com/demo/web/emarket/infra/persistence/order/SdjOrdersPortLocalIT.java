package com.demo.web.emarket.infra.persistence.order;

import com.demo.web.emarket.domain.Address;
import com.demo.web.emarket.domain.UniqueId;
import com.demo.web.emarket.domain.customer.*;
import com.demo.web.emarket.domain.order.Order;
import com.demo.web.emarket.domain.order.Line;
import com.demo.web.emarket.domain.order.OrderStatus;
import com.demo.web.emarket.domain.order.Quantity;
import com.demo.web.emarket.infra.persistence.InfraLocalIT;
import com.demo.web.emarket.infra.persistence.customer.CustomersSdj;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;

public class SdjOrdersPortLocalIT extends InfraLocalIT {
    @Autowired
    private SdjOrdersPort sut;

    @Autowired
    private OrdersJpaRepo ordersJpaRepo;
    @Autowired private CustomersSdj customersJpaRepo;
    private Order order;

    @Before
    public void setUp() {
        cleanUp();

        Customer someCustomer = someCustomer();
        customersJpaRepo.saveAndFlush(someCustomer);
        order = someOrder(someCustomer);
    }

    @Test
    public void getOrThrow() {
        Order expectedOrder = ordersJpaRepo.saveAndFlush(order);
        Order actualOrder = sut.getOrThrow(order.getId());
        assertThat(actualOrder).isEqualToComparingFieldByFieldRecursively(expectedOrder);
    }

    @Test
    public void add() {
        Order actualOrder = sut.add(order);
        Order expectedOrder = ordersJpaRepo.getOne(order.getId());
        assertThat(actualOrder).isEqualToComparingFieldByFieldRecursively(expectedOrder);
    }

    @Test
    public void getAll() {
        Order expectedOrder = ordersJpaRepo.saveAndFlush(this.order);
        Set<Order> all = sut.getAll();
        assertThat(all)
                .usingFieldByFieldElementComparator()
                .containsExactly(expectedOrder);
    }

    private Customer someCustomer() {
        CustomerName fullName = new CustomerName(new NamePart("John"), new NamePart("Doe"));
        return new Customer(fullName, new Address("Happy stree", 25),
                new PhoneNumber("+07222222"), new HistoricData(now(), now()), emptySet());
    }

    private void cleanUp() {
        customersJpaRepo.deleteAll();
        ordersJpaRepo.deleteAll();
    }

    private Order someOrder(Customer someCustomer) {
        Line line = new Line(new Quantity(1), new UniqueId());
        return new Order(asList(line), OrderStatus.INITIATED, someCustomer.getId());
    }
}
