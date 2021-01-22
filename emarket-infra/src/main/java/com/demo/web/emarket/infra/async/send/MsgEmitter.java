package com.demo.web.emarket.infra.async.send;

import com.demo.web.emarket.infra.async.kafka.stub.KafkaClientProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MsgEmitter<T> {

    private static Logger logger = LoggerFactory.getLogger(MsgEmitter.class);

    @Autowired
    private KafkaClientProducer kafkaClientProducer;
    @Autowired
    private EmitterInRepository emitterInRepository;


    public void start(){
        logger.info("MsgEmitter is starting...");
        this.kafkaClientProducer.start();
        logger.info("MsgEmitter started!");
    }

    public void stop(){
        logger.info("MsgEmitter is stopping...");
        this.kafkaClientProducer.stop();
        logger.info("MsgEmitter stoped!");
    }

    public LocalDateTime emit(T t, String transactionId, String topic){
        return this.emitterInRepository.emit(t, transactionId, topic);
    }
}
