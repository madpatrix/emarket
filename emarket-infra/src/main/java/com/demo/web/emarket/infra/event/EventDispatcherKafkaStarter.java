package com.demo.web.emarket.infra.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.atomic.AtomicBoolean;

@Profile("EVENT_DISPATCHER_KAFKA")
@Component
public class EventDispatcherKafkaStarter implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger(EventDispatcherKafkaStarter.class);

    @Autowired
    private DomainEventDispatcherKafkaImpl domainEventDispatcherKafka;
    public static final AtomicBoolean alreadyRunning = new AtomicBoolean(false);

    @PreDestroy
    private void destroy(){
        this.domainEventDispatcherKafka.destroy();
    }


    @Override
    public void run(String... args) throws Exception {

        if (alreadyRunning.get()){
            logger.warn("already running: EventDispatcherKafkaStarter");
        }

        this.domainEventDispatcherKafka.init();
        alreadyRunning.set(true);
    }
}
