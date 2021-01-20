package com.demo.web.emarket.domain.ddd.event;

import com.demo.web.emarket.domain.ddd.BaseEntity;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@EntityScan
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AttachedAggregateRoot {
    Class<? extends BaseEntity<?,?>> value();
}
