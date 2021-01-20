package com.demo.web.emarket.infra.event;

import com.demo.web.emarket.domain.ddd.event.AttachedAggregateRoot;
import com.demo.web.emarket.domain.ddd.event.DomainEvent;

public class MissingAttachedAggregateRootException extends RuntimeException{

    public MissingAttachedAggregateRootException(DomainEvent domainEvent) {
        super(String.format("%s annotation is missing for domain event %s",
                AttachedAggregateRoot.class.getCanonicalName(),
                domainEvent.getClass().getCanonicalName())
        );
    }
}
