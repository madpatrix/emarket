package com.demo.web.emarket.infra.event;

import com.demo.web.emarket.domain.ddd.event.DomainEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "kafka")
public class ConfKafkaDomainEventTopicName {

    private Map<Class<? extends DomainEvent>, String> domainEventTopicNameMap = new HashMap<>();

    public Map<Class<? extends DomainEvent>, String> getDomainEventTopicNameMap(){return domainEventTopicNameMap;}

    public String resolveTopicName(Class<? extends DomainEvent> domainEventType){
        return this.getDomainEventTopicNameMap().get(domainEventType);
    }
}
