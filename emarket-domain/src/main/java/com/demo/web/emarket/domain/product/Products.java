package com.demo.web.emarket.domain.product;

import com.demo.web.emarket.domain.UniqueId;
import com.demo.web.emarket.domain.ddd.DDD;

import java.util.Set;

@DDD.DomainRepository
public interface Products {
    Set<Product> findAll(Set<UniqueId> productIds);
    Product getOrThrow(UniqueId productId);
    Product add(Product product);
}
