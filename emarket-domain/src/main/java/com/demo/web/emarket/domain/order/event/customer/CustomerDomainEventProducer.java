package com.demo.web.emarket.domain.order.event.customer;

import com.demo.web.emarket.domain.customer.Customer;
import com.demo.web.emarket.domain.ddd.DDD;
import com.demo.web.emarket.domain.order.Order;
import com.demo.web.emarket.domain.order.event.customer.model.CustomerModel;
import com.demo.web.emarket.domain.order.event.order.OrderSingleProduct;
import com.demo.web.emarket.domain.order.event.order.model.OrderModel;
import org.springframework.stereotype.Service;

@DDD.DomainService
@Service
public class CustomerDomainEventProducer {

    public CustomerAdded customerAdded(Customer customer){
        CustomerModel customerModel = new CustomerModel();
        customerModel.firstName = customer.getFirstName().getValue();
        customerModel.lastName = customer.getLastName().getValue();

        return new CustomerAdded(customerModel);
    }
}
