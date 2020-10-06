package com.demo.web.emarket.infra.persistence.customer;

import com.demo.web.emarket.domain.UniqueId;
import com.demo.web.emarket.domain.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface CustomersSdj extends JpaRepository<Customer, UniqueId> {
    Set<Customer> findByNameFirstNameValueContaining(String namePart);
}
