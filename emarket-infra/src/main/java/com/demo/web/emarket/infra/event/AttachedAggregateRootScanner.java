package com.demo.web.emarket.infra.event;

import com.demo.web.emarket.domain.ddd.BaseEntity;
import com.demo.web.emarket.domain.ddd.event.AttachedAggregateRoot;
import com.demo.web.emarket.domain.ddd.event.DomainEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttachedAggregateRootScanner {
    private static Logger logger = LoggerFactory.getLogger(AttachedAggregateRootScanner.class);

    private DomainEvent domainEvent;

    public AttachedAggregateRootScanner(DomainEvent domainEvent) {
        this.domainEvent = domainEvent;
    }

    public Class<? extends BaseEntity<?,?>> getAggregateRootType(){
        AttachedAggregateRoot annotation = domainEvent.getClass().getAnnotation(AttachedAggregateRoot.class);
        if(annotation==null){
            final MissingAttachedAggregateRootException e = new MissingAttachedAggregateRootException(domainEvent);
            logger.error("", e);
            throw e;
        }

        return annotation.value();
    }
}
