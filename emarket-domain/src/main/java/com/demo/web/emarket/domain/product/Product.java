package com.demo.web.emarket.domain.product;

import com.demo.web.emarket.domain.ddd.BaseAggregateRoot;
import com.demo.web.emarket.domain.UniqueId;
import com.demo.web.emarket.domain.ddd.DDD;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@DDD.AggregateRoot
public class Product extends BaseAggregateRoot<Product, UniqueId> {
    @NotNull
    private Price price;
    private LocalDate localDate;

    private Product(UniqueId id, Price price, LocalDate localDate) {
        super(Product.class, id);
        this.price = price;
        this.localDate = localDate;
    }

    public Product(Price price) {
        this(new UniqueId(), price, LocalDate.now().plusYears(30));
    }


    public Price getPrice() {
        return price;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setId(UniqueId id){

    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    /*Used by JPA dont use in production code*/
    private Product() {
        super(Product.class);
        this.price = null;
    }

    public Price price() {
        return price;
    }
}
