package com.demo.web.emarket.infra.event;

import com.demo.web.emarket.domain.ddd.DDD;
import com.demo.web.emarket.domain.ddd.event.DomainEvent;
import com.demo.web.emarket.infra.async.receive.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@DDD.InfrastructureServiceImpl
@Service
@Primary
public class DomainEventSubscriberKafkaImpl {

    private static Logger logger = LoggerFactory.getLogger(DomainEventSubscriberKafkaImpl.class);

    @Autowired
    private MsgHandler msgHandler;

    public void init(){
        this.msgHandler.storeType(ReceivedMsgStore.StoreType.REPOSITORY_STORE).start();
    }

    public void destroy(){
        logger.debug("Destroy Kafka Subscriber.");
        this.msgHandler.stop();
    }

    public void registerDomainEventJsonHandler(String topicName, String msgType, JsonHandler jsonHandler){
        logger.debug("registerDomainEventJsonHandler({}, {}, {})", topicName, msgType, jsonHandler);
        this.msgHandler.registerJsonHandler(topicName, msgType, jsonHandler);
    }

    public <T extends DomainEvent> void registerDomainEventJsonTypeHandler(String topicName, Class<?> domainEventType, JsonTypeHandler jsonTypeHandler){
        logger.debug("registerDomainEventJsonTypeHandler({}, {}, {})", topicName, domainEventType, jsonTypeHandler);
        this.msgHandler.registerJsonTypeHandler(topicName, domainEventType, jsonTypeHandler);
    }

    public <T extends DomainEvent> void registerDomainEventObjectHandler(String topicName, Class<?> domainEventType, ObjectHandler<T> objectHandler){
        logger.debug("registerDomainEventObjectHandler({}, {}, {})", topicName, domainEventType, objectHandler);
        this.msgHandler.registerObjectHandler(topicName, domainEventType, objectHandler);
    }

    public <T extends DomainEvent> void registerDomainEventObjectAndJsonHandler(String topicName, Class<?> domainEventType, ObjectHandler<T> objectHandler, JsonHandler jsonHandler){
        logger.debug("registerDomainEventObjectAndJsonHandler({}, {}, {}, {})", topicName, domainEventType, objectHandler, jsonHandler);
        this.msgHandler.registerObjectAndJsonHandler(topicName, domainEventType, objectHandler, jsonHandler);
    }


}
