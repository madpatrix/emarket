package com.demo.web.emarket.domain.product;

import com.demo.web.emarket.domain.UniqueId;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class InMemoryProductsPort implements ProductsPort {
    private Set<Product> db = new HashSet<>();
    @Override
    public Set<Product> findAll(Set<UniqueId> productIds) {
        return db.stream()
                .filter(p -> productIds.contains(p.getId()))
                .collect(toSet());
    }

    @Override
    public Product getOrThrow(UniqueId productId) {
        return db.stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst().orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Product add(Product product) {
        db.remove(product);
        db.add(product);
        return product;
    }
}
