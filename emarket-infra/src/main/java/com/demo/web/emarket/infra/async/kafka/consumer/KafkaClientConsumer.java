package com.demo.web.emarket.infra.async.kafka.consumer;

import com.demo.web.emarket.infra.async.MsgEnvelope;
import com.demo.web.emarket.infra.async.receive.MsgReceptor;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class KafkaClientConsumer {


    @Value("${kafka.hosts}")
    private String hostKafka;

    @Value("${kafka.group.id:group_id}")
    private String groupIdKafka;

    private AtomicBoolean start = new AtomicBoolean(false);
    private AtomicBoolean close = new AtomicBoolean(false);

    private MsgReceptor msgReceptor;
    private KafkaConsumer<String, MsgEnvelope> kafkaConsumer;
    private ScheduledExecutorService scheduledExecutorService;

    @Value("${kafka.sslKeystoreLocationConfig: }")
    private String sslKeystoreLocationConfig;
    @Value("${kafka.sslKeystorePasswordConfig: }")
    private String sslKeystorePasswordConfig;
    @Value("${kafka.sslKeyPasswordConfig: }")
    private String sslKeyPasswordConfig;
    @Value("${kafka.sslTruststoreLocationConfig: }")
    private String sslTruststoreLocationConfig;
    @Value("${kafka.sslTruststorePasswordConfig: }")
    private String sslTruststorePasswordConfig;

    @Value("${kafka.sslActivate}")
    private String sslActivate;//TODO: replace with boolean

    public void start(List<String> topics, MsgReceptor msgReceptor){
        start(topics, false, msgReceptor);
    }

    public void stop(){
        this.start.set(false);
        this.close.set(true);
        this.scheduledExecutorService.shutdown();
        this.kafkaConsumer.close();
    }

    public void start(List<String> topics, boolean seekToEndBeforeStart, MsgReceptor msgReceptor) {
        this.msgReceptor = msgReceptor;

        this.configureConsumer();

        this.kafkaConsumer.subscribe(topics);

        if(seekToEndBeforeStart){
            this.kafkaConsumer.seekToEnd(Collections.emptyList());
            this.kafkaConsumer.poll(1000);
        }

        this.start.set(true);

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(
                this::receive,
                0,
                1000,
                TimeUnit.MILLISECONDS
        );
    }


    private void configureConsumer() {
        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, hostKafka);
        configProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonMsgEnvelopDeserializer.class);
        configProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "all");

        configureSsl(configProperties);
        this.kafkaConsumer = new KafkaConsumer<String, MsgEnvelope>(configProperties);
    }

    private void configureSsl(Properties configProperties) {
        if(Boolean.getBoolean(sslActivate)){
            configProperties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
            configProperties.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, sslKeystoreLocationConfig);
            configProperties.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, sslKeystorePasswordConfig);
            configProperties.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, sslKeyPasswordConfig);
            configProperties.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, sslTruststoreLocationConfig);
            configProperties.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, sslTruststorePasswordConfig);

        }
    }



    private void receive() {
        if(this.close.get()){
            this.kafkaConsumer.close();
            return;
        }

        if(!this.start.get()){
            return;
        }

        ConsumerRecords<String, MsgEnvelope> msgEnvelopeConsumerRecords = this.kafkaConsumer.poll(100);
        final List<MsgEnvelope> msgEnvelopes = new ArrayList<>(msgEnvelopeConsumerRecords.count());
        for(ConsumerRecord<String, MsgEnvelope> msgEnvelopeConsumerRecord : msgEnvelopeConsumerRecords){
            MsgEnvelope msgEnvelope = msgEnvelopeConsumerRecord.value();
            if(msgEnvelope != null && msgEnvelope.getId() != null){
                msgEnvelopes.add(msgEnvelope);
            }
        }

        this.msgReceptor.receive(msgEnvelopes);

    }


    public boolean isStarted(){return this.start.get();}

}
