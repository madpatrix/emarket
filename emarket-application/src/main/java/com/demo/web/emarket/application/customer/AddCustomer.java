package com.demo.web.emarket.application.customer;

import com.demo.web.emarket.application.ApplicationService;
import com.demo.web.emarket.application.order.OrderSingleProductCommand;
import com.demo.web.emarket.domain.UniqueId;
import com.demo.web.emarket.domain.customer.Customer;
import com.demo.web.emarket.domain.customer.Customers;
import com.demo.web.emarket.domain.ddd.DDD;
import com.demo.web.emarket.domain.order.Line;
import com.demo.web.emarket.domain.order.Order;
import com.demo.web.emarket.domain.order.OrderStatus;
import com.demo.web.emarket.domain.order.OrdersPort;
import com.demo.web.emarket.domain.order.event.customer.CustomerDomainEventHandler;
import com.demo.web.emarket.domain.order.event.order.OrderDomainEventHandler;

import static java.util.Arrays.asList;

@DDD.ApplicationService
@ApplicationService
public class AddCustomer {
    private final Customers customers;
    private final CustomerDomainEventHandler customerDomainEventHandler;

    public AddCustomer(Customers customers, CustomerDomainEventHandler customerDomainEventHandler) {
        this.customers = customers;
        this.customerDomainEventHandler = customerDomainEventHandler;
    }

    public UniqueId addCustomer(Customer customer) {
        this.customerDomainEventHandler.generateCustomerAddedDomainEvent(customer);
        Customer newCustomer = this.customers.add(customer);

        return newCustomer.getId();
    }

}
