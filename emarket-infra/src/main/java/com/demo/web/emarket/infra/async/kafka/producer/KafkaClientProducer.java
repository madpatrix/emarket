package com.demo.web.emarket.infra.async.kafka.producer;

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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class KafkaClientProducer {

    private static AtomicLong countSentMsgContainer = new AtomicLong();
    private AtomicBoolean start = new AtomicBoolean(false);

    private Logger logger = LoggerFactory.getLogger(KafkaClientProducer.class);

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

    @PersistenceContext
    EntityManager entityManager;

    public KafkaClientProducer(SentMsgState sentMsgState) {
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
    int count = 0;
    private void send() {

        System.out.println(LocalDateTime.now() + " Producing started for: " + count);
        List<SentMsgConainer> msgToBlock = findNextMessageToSend();

        while(msgToBlock.size()>0) {
            List<String> blockedTopics = new ArrayList<>();

            List<SentMsgConainer> blockedMessages = new ArrayList<>();

            for(SentMsgConainer msg : msgToBlock) {
                if(!blockedTopics.contains(msg.getTopic())) {
                    long start = System.currentTimeMillis();
                    int blocked = blockMessage(msg);
                    System.out.println("Time to block 1 msg: "+ (System.currentTimeMillis()-start));
                    if (blocked > 0) {
                        blockedMessages.add(msg);
                    } else {
                        blockedTopics.add(msg.getTopic());
                    }
                }
            }

            if(blockedMessages.size()>0){
                for(SentMsgConainer msgToSend: blockedMessages) {
                    sendMsgToKafka(msgToSend, count);
                }
            }

            msgToBlock = findNextMessageToSend();
        }
        count++;
        /*try {
            TimeUnit.SECONDS.sleep(60);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

        /*
        logger.debug("find topic list from tmp table.");
        List<String> topics = this.sendMsgContainerRepository.findTopicList();
        logger.trace("topic list from tmp table: ", topics);
        //TESTING
        List<String> topicsToSearch = this.sendMsgContainerRepository.findDistinctTopicByBlockTimeIsNullOrBlockTimeLessThan(LocalDateTime.now());
        this.sendMsgContainerRepository.findFirstByTopicInAndBlockTimeIsNullOrTopicInAndBlockTimeLessThanOrderByCreationTimeDesc(topicsToSearch,topicsToSearch, LocalDateTime.now());

        this.sentMsgState.resetSendingErrorForAllTopics();

        for(String topic: topics){
            final List<SentMsgConainer> sentMsgConainers = this.sendMsgContainerRepository.findFirst1000ByStatusAndTopicOrderByCreationTimeAsc(SentMsgConainer.Status.WAITING_SENDING, topic);
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
        */
    }


    private void sendMsgToKafka(SentMsgConainer msgToBlock, int count) {
        final MsgEnvelope msgEnvelope = msgToBlock.asMsgEnvelope();
        System.out.println(LocalDateTime.now() + " sending to kafka: "+count);
        producer.send(getProducerRecord(msgEnvelope), getCallback(msgToBlock.getTopic(), msgEnvelope, count));
    }

    @Transactional
    private int blockMessage(SentMsgConainer msgToBlock){
            if(msgToBlock.getBlockTime() == null){
              return this.sendMsgContainerRepository.setMessageBlockTimeWhereActualBlockTimeIsNull(msgToBlock.getId(), LocalDateTime.now());
            }else {
              return this.sendMsgContainerRepository.setMessageBlockTime(msgToBlock.getId(), msgToBlock.getBlockTime(), LocalDateTime.now());
            }
    }

    private List<SentMsgConainer> findNextMessageToSend() {
        long start = System.currentTimeMillis();
        //List<SentMsgConainer> msgToBlock = this.sendMsgContainerRepository.findMessagesToSend(LocalDateTime.now().minusSeconds(5), 1000);
        List<SentMsgConainer> msgToBlock = entityManager.createQuery("SELECT s FROM SentMsgConainer s WHERE (s.blockTime is null OR s.blockTime < :nowMinus5Sec) AND s.topic NOT IN (SELECT sm.topic FROM SentMsgConainer sm WHERE sm.blockTime IS NOT NULL AND sm.blockTime > :nowMinus5Sec) ORDER BY s.creationTime ASC", SentMsgConainer.class)
                .setParameter("nowMinus5Sec", LocalDateTime.now().minusSeconds(5))
                .setMaxResults(10)
                .setFirstResult(0)
                .getResultList();
        System.out.println("Time to find 1 msg: "+ (System.currentTimeMillis()-start));
        return msgToBlock;
    }
    @Transactional
    private Callback getCallback(String topic, MsgEnvelope msgEnvelope, int count) {
        System.out.println(LocalDateTime.now() + " Callback send to kafka: "+count);
        return ((recordMetadata, e) -> {
            if(e==null){
                logger.info("sent {}: { topic: {}, tx-id: {}, partition: {}, offset: {} }",
                        msgEnvelope.getMsgType(),
                        msgEnvelope.getTopic(),
                        msgEnvelope.getTransactionId(),
                        recordMetadata.partition(),
                        recordMetadata.offset());

                sendMsgContainerRepository.deleteById(msgEnvelope.getId());
                sentMsgState.sent(1);
                if(countDownLatch != null){
                    countDownLatch.countDown();
                }
                countSentMsgContainer.addAndGet(1);

            }else{
                sentMsgState.setSendingErrorForTopic(topic);
                logger.error(String.format("Error occurred when tried to send message with id: $s, topic: $s", msgEnvelope.getId(), msgEnvelope.getTopic()), e);
            }
            System.out.println(LocalDateTime.now() + " Producing ended for: "+ count);
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
                1000,
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
