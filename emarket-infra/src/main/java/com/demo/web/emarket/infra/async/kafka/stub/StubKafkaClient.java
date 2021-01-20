package com.demo.web.emarket.infra.async.kafka.stub;

import com.demo.web.emarket.infra.async.MsgEnvelope;
import com.demo.web.emarket.infra.async.send.SendMsgContainerRepository;
import com.demo.web.emarket.infra.async.send.SentMsgConainer;
import com.demo.web.emarket.infra.async.send.SentMsgState;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class StubKafkaClient {

    private static AtomicLong countSentMsgContainer = new AtomicLong();
    private AtomicBoolean start = new AtomicBoolean(false);

    private Logger logger = LoggerFactory.getLogger(StubKafkaClient.class);

    @Value("${kafka.hosts}")
    private String hostKafka;

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
    private SendMsgContainerRepository sendMsgContainerRepository;
    private CountDownLatch countDownLatch;
    private SentMsgState sentMsgState;
    private KafkaProducer<String, MsgEnvelope> producer;
    private ScheduledExecutorService scheduledExecutorService;

    public StubKafkaClient(SentMsgState sentMsgState) {
        this.sentMsgState = sentMsgState;
    }

    private void configureProducer(){
        Properties configProperties = new Properties();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, hostKafka);
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonMsgEnvelopSerializer.class);
        configProperties.put(ProducerConfig.ACKS_CONFIG, "all");

        configureSsl(configProperties);

        producer = new KafkaProducer<String, MsgEnvelope>(configProperties);
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

    private void checkSendMsgContainerRepository(){
        send();
    }

    private void send() {
        logger.debug("find topic list from tmp table.");
        List<String> topics = this.sendMsgContainerRepository.findTopicList();
        logger.trace("topic list from tmp table: ", topics);

        this.sentMsgState.resetSendingErrorForAllTopics();

        for(String topic: topics){
            final List<SentMsgConainer> sentMsgConainers = this.sendMsgContainerRepository.findFirst1000ByStatusAndTopic(SentMsgConainer.Status.WAITING_SENDING, topic);
            logger.debug("Found {} SentMsgContainer items with status {} on topic {}", sentMsgConainers.size(), SentMsgConainer.Status.WAITING_SENDING, topic);

            for(SentMsgConainer sentMsgConainer: sentMsgConainers){
                if(this.sentMsgState.isSendingErrorForTopic(topic)){
                    logger.warn("Sending error occurred for topic: {}, skipping to the next topic", topic);
                    break;
                }

                final MsgEnvelope msgEnvelope = sentMsgConainer.asMsgEnvelope();

                producer.send(getProducerRecord(msgEnvelope), getCallback(topic, msgEnvelope));
            }
        }
    }

    private Callback getCallback(String topic, MsgEnvelope msgEnvelope) {
        return ((recordMetadata, e) -> {
            if(e==null){
                logger.info("sent {}: { topic: {}, tx-id: {}, partition: {}, offset: {} }",
                        msgEnvelope.getMsgType(),
                        msgEnvelope.getTopic(),
                        msgEnvelope.getTransactionId(),
                        recordMetadata.partition(),
                        recordMetadata.offset());

                sendMsgContainerRepository.delete(msgEnvelope.getId());
                sentMsgState.sent(1);
                if(countDownLatch != null){
                    countDownLatch.countDown();
                }
                countSentMsgContainer.addAndGet(1);
            }else{
                sentMsgState.setSendingErrorForTopic(topic);
                logger.error(String.format("Error occurred when tried to send message with id: $s, topic: $s", msgEnvelope.getId(), msgEnvelope.getTopic()), e);
            }
        });
    }

    private ProducerRecord<String, MsgEnvelope> getProducerRecord(MsgEnvelope msgEnvelope) {
        return new ProducerRecord<>(msgEnvelope.getTopic(), msgEnvelope.getTransactionId(), msgEnvelope);
    }

    public void setCountDownLatch(final CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    public void start(){
        if(isStarted()){
            throw new IllegalStateException(this.getClass().getSimpleName()+" is already started");
        }

        this.configureProducer();

        long numberOfMessages = this.sendMsgContainerRepository.countByStatus(SentMsgConainer.Status.WAITING_SENDING);
        logger.info("{} of messages found in TMP table.");

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(
                this::checkSendMsgContainerRepository,
                0,
                10000,
                TimeUnit.MILLISECONDS
        );
    }

    public void stop(){
        this.start.set(false);
        this.producer.close();
        scheduledExecutorService.shutdown();
    }

    public boolean isStarted(){
        return this.start.get();
    }
}
