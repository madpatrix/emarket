package com.demo.web.emarket.domain.order.event.customer;

import com.demo.web.emarket.domain.customer.Customer;
import com.demo.web.emarket.domain.ddd.DDD;
import com.demo.web.emarket.domain.ddd.event.DomainEventDispatcher;
import com.demo.web.emarket.domain.order.Order;
import com.demo.web.emarket.domain.order.event.order.OrderDomainEventProducer;
import com.demo.web.emarket.domain.order.event.order.OrderSingleProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@DDD.DomainService
@Service
public class CustomerDomainEventHandler {

    @Autowired
    private CustomerDomainEventProducer customerDomainEventProducer;
    @Autowired
    private DomainEventDispatcher domainEventDispatcher;

    public void generateCustomerAddedDomainEvent(Customer customer){
        CustomerAdded customerAdded = customerDomainEventProducer.customerAdded(customer);
        domainEventDispatcher.dispatch(customerAdded, Customer.class);
    }
}
