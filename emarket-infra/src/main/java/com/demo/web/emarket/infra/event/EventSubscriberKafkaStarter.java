package com.demo.web.emarket.infra.event;

import com.demo.web.emarket.domain.order.event.customer.CustomerAdded;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.atomic.AtomicBoolean;

@Profile("EVENT_SUBSCRIBER_KAFKA")
@Component
public class EventSubscriberKafkaStarter implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger(EventSubscriberKafkaStarter.class);

    @Autowired
    private DomainEventSubscriberKafkaImpl eventSubscriberKafka;
    @Autowired
    private ConfKafkaDomainEventTopicName confKafkaDomainEventTopicName;
    public static final AtomicBoolean alreadyRunning = new AtomicBoolean(false);



    @PreDestroy
    private void destroy(){
        this.eventSubscriberKafka.destroy();
    }


    @Override
    public void run(String... args) throws Exception {

        if (alreadyRunning.get()){
            logger.warn("already running: DomainEventSubscriberKafka");
        }
        subscribe();
        this.eventSubscriberKafka.init();
        alreadyRunning.set(true);
    }

    public void subscribe(){
        this.eventSubscriberKafka.registerDomainEventJsonHandler(
            this.confKafkaDomainEventTopicName.resolveTopicName(CustomerAdded.class),
            CustomerAdded.class.getCanonicalName(),
                (json, msgId) ->
                    System.out.println(
                            String.format("######## Customer got from kafka : %s, %s", msgId, json)
                    )

        );
    }
}
