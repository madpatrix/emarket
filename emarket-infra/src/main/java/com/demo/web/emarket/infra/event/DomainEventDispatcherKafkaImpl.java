package com.demo.web.emarket.infra.event;

import com.demo.web.emarket.domain.ddd.BaseAggregateRoot;
import com.demo.web.emarket.domain.ddd.DDD;
import com.demo.web.emarket.domain.ddd.event.DomainEvent;
import com.demo.web.emarket.domain.ddd.event.DomainEventDispatcher;
import com.demo.web.emarket.infra.async.send.MsgEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

@DDD.InfrastructureServiceImpl
@Service
public class DomainEventDispatcherKafkaImpl implements DomainEventDispatcher{

    ThreadLocal<String> transactionId = ThreadLocal.withInitial(DomainEventDispatcherKafkaImpl::newTxIdValue);

    private static String newTxIdValue(){
        return System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0,10);
    }

    private static Logger logger = LoggerFactory.getLogger(DomainEventDispatcherKafkaImpl.class);

    @Autowired
    private ConfKafkaDomainEventTopicName confKafkaDomainEventTopicName;
    @Autowired
    private MsgEmitter<DomainEvent> msgEmitter;

    protected void init(){
        logger.debug("initialization of DomainEventDispatcherKafka");
        this.msgEmitter.start();
    }


    @Override
    public void dispatch(DomainEvent domainEvent) {
        logger.debug("dispatch({})", domainEvent);
        final String topic = new AttachedAggregateRootScanner(domainEvent).getAggregateRootType().getCanonicalName();

        this.msgEmitter.emit(domainEvent, transactionId.get(), topic);

        registerSynchronizationForNewTxIdValue();
    }

    private void registerSynchronizationForNewTxIdValue() {
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCommit() {
                        transactionId.set(newTxIdValue());
                    }
                });
    }


    @Override
    public void dispatch(DomainEvent domainEvent, Class<? extends BaseAggregateRoot<?, ?>> aggregateRootType) {
    logger.debug("dispatch({} , {})", domainEvent, aggregateRootType);
    String topic = confKafkaDomainEventTopicName.resolveTopicName(domainEvent.getClass());

    if(topic == null || topic.isEmpty()){
        topic = aggregateRootType.getCanonicalName();
    }

    this.msgEmitter.emit(domainEvent, transactionId.get(), topic);

        registerSynchronizationForNewTxIdValue();
    }

    @Override
    public void dispatch(DomainEvent domainEvent, Class<? extends BaseAggregateRoot<?, ?>> aggregateRootType, String topicSuffix) {
        logger.debug("dispatch({} , {}, {})", domainEvent, aggregateRootType, topicSuffix);

        String topic = aggregateRootType.getCanonicalName() + topicSuffix;

        this.msgEmitter.emit(domainEvent, transactionId.get(), topic);

        registerSynchronizationForNewTxIdValue();
    }

    protected void destroy(){
        logger.debug("destroy of DomainEventDispatcherKafka");
        this.msgEmitter.stop();
    }
}
