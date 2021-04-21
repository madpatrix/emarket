package com.demo.web.emarket.infra.async.kafka.consumer;

import com.demo.web.emarket.domain.ddd.ULID;
import com.demo.web.emarket.infra.async.MsgEnvelope;
import com.demo.web.emarket.infra.async.receive.*;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class KafkaClientConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaClientConsumer.class);

    @Value("${kafka.hosts}")
    private String hostKafka;

    @Value("${kafka.group.id:group_id}")
    private String groupIdKafka;

    @Value("${kafka.consumer.retriesNo:5}")
    private int maxRetriesNo;
    @Value("${kafka.consumer.retry.timeout:5}")
    private int retryTimeout;

    private AtomicBoolean start = new AtomicBoolean(false);
    private AtomicBoolean close = new AtomicBoolean(false);

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

    @Autowired
    private MsgHandlerFromStore msgHandlerFromStore;
    @Autowired
    private ReceivedUltRepository receivedUltRepository;

    public void stop(){
        this.start.set(false);
        this.close.set(true);
        this.scheduledExecutorService.shutdown();
        this.kafkaConsumer.close();
    }

    public void start(List<String> topics, boolean seekToEndBeforeStart) {
        this.configureConsumer();

        this.kafkaConsumer.subscribe(topics);

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
        configProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        configProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

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

        ConsumerRecords<String, MsgEnvelope> msgEnvelopeConsumerRecords = this.kafkaConsumer.poll(1000);
        List<TopicPartition> blockedTopicPartition = new ArrayList<>();
        for(ConsumerRecord<String, MsgEnvelope> msgEnvelopeConsumerRecord : msgEnvelopeConsumerRecords){
            if(blockedTopicPartition.contains(new TopicPartition(msgEnvelopeConsumerRecord.topic(), msgEnvelopeConsumerRecord.partition()))){
                continue;
            }
            Optional<ReceivedUlt> receivedUltOptional = receivedUltRepository.findById(new ReceivedUltId(msgEnvelopeConsumerRecord.topic(), msgEnvelopeConsumerRecord.partition()));

            MsgEnvelope msgEnvelope = msgEnvelopeConsumerRecord.value();
            if(msgEnvelope != null && msgEnvelope.getId() != null){
                if(!receivedUltOptional.isPresent() || ULID.parseULID(receivedUltOptional.get().getUlid()).compareTo(ULID.parseULID(msgEnvelope.getId())) < 0 ) {
                handleMesage(blockedTopicPartition,
                        msgEnvelopeConsumerRecord,
                        receivedUltOptional.orElseGet(() -> new ReceivedUlt(msgEnvelope.getId(), new ReceivedUltId(msgEnvelopeConsumerRecord.topic(), msgEnvelopeConsumerRecord.partition()), msgEnvelopeConsumerRecord.offset())),
                        msgEnvelope);
                }
                else if(ULID.parseULID(receivedUltOptional.get().getUlid()).compareTo(ULID.parseULID(msgEnvelope.getId())) > 0){
                    updateMessageOffset(msgEnvelopeConsumerRecord, msgEnvelope);
                    LOGGER.warn("Lost order or duplicate message received from kafka having ID: kafkaKey={}, messageId={}",msgEnvelopeConsumerRecord.key(), msgEnvelope.getId());
                }
                else if(ULID.parseULID(receivedUltOptional.get().getUlid()).equals(ULID.parseULID(msgEnvelope.getId()))){
                    if(receivedUltOptional.get().getRetriesNo() == 0 || receivedUltOptional.get().getRetriesNo()>maxRetriesNo){
                        updateMessageOffset(msgEnvelopeConsumerRecord, msgEnvelope);
                    }
                    else{
                        if(receivedUltOptional.get().getTs().isBefore(LocalDateTime.now().minusSeconds(retryTimeout))){
                            blockedTopicPartition.add(new TopicPartition(msgEnvelopeConsumerRecord.topic(), msgEnvelopeConsumerRecord.partition()));
                        }else{
                            handleMesage(blockedTopicPartition,
                                    msgEnvelopeConsumerRecord,
                                    receivedUltOptional.orElseGet(() -> new ReceivedUlt(msgEnvelope.getId(), new ReceivedUltId(msgEnvelopeConsumerRecord.topic(), msgEnvelopeConsumerRecord.partition()), msgEnvelopeConsumerRecord.offset())),
                                    msgEnvelope);
                        }
                    }
                }
            }
        }

        commitKafkaOffsetForAllTopicsAndPartitions(receivedUltRepository.findAll());
    }

    private void handleMesage(List<TopicPartition> blockedTopicPartition, ConsumerRecord<String, MsgEnvelope> msgEnvelopeConsumerRecord, ReceivedUlt receivedUlt, MsgEnvelope msgEnvelope) {
        try {
            consumeMessageFromKafka(msgEnvelopeConsumerRecord, msgEnvelope);
        }catch (Exception e){
            blockedTopicPartition.add(new TopicPartition(msgEnvelopeConsumerRecord.topic(), msgEnvelopeConsumerRecord.partition()));
            handleConsumerError(receivedUlt);
        }
    }

    @Transactional
    private void handleConsumerError(ReceivedUlt receivedUlt) {
        receivedUlt.incrementRetriesNo();
        receivedUltRepository.save(receivedUlt);
    }

    @Transactional
    private void consumeMessageFromKafka(ConsumerRecord<String, MsgEnvelope> msgEnvelopeConsumerRecord, MsgEnvelope msgEnvelope) {
        msgHandlerFromStore.schedule(msgEnvelope);
        receivedUltRepository.save(new ReceivedUlt(msgEnvelope.getId(), new ReceivedUltId(msgEnvelopeConsumerRecord.topic(), msgEnvelopeConsumerRecord.partition()), msgEnvelopeConsumerRecord.offset()));
    }

    @Transactional
    private void updateMessageOffset(ConsumerRecord<String, MsgEnvelope> msgEnvelopeConsumerRecord, MsgEnvelope msgEnvelope) {
        receivedUltRepository.save(new ReceivedUlt(msgEnvelope.getId(), new ReceivedUltId(msgEnvelopeConsumerRecord.topic(), msgEnvelopeConsumerRecord.partition()), msgEnvelopeConsumerRecord.offset()));
    }

    private void commitKafkaOffsetForAllTopicsAndPartitions(List<ReceivedUlt> receivedUlts) {
        Map<TopicPartition, OffsetAndMetadata> commited = new HashMap<>();
        receivedUlts.forEach(ru -> commited.put(new TopicPartition(ru.getId().getTopic(), ru.getId().getPartition()), new OffsetAndMetadata(ru.getMsgOffset())));
        kafkaConsumer.commitSync(commited);
    }


    public boolean isStarted(){return this.start.get();}

}
