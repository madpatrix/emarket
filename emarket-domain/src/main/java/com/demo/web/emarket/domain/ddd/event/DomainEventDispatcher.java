package com.demo.web.emarket.domain.ddd.event;

import com.demo.web.emarket.domain.ddd.BaseAggregateRoot;
import com.demo.web.emarket.domain.ddd.DDD;

@DDD.InfrastructureService
public interface DomainEventDispatcher {

    void dispatch(DomainEvent domainEvent);
    void dispatch(DomainEvent domainEvent, Class<? extends BaseAggregateRoot<?,?>> aggregateRootType);
    void dispatch(DomainEvent domainEvent, Class<? extends BaseAggregateRoot<?,?>> aggregateRootType, String topicSuffix);
}
