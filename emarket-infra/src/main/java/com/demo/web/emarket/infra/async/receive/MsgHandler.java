package com.demo.web.emarket.infra.async.receive;

import com.demo.web.emarket.infra.async.kafka.consumer.KafkaClientConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MsgHandler {

    private static final String CANNOT_REGISTER_ERROR = "Cannot register message after MsgHandler is started.";

    @Autowired
    private KafkaClientConsumer kafkaClientConsumer;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ReceivedMsgContainerRepository receivedMsgContainerRepository;
    @Autowired
    private ReceivedMsgState receivedMsgState;

    private MsgHandlerFromStore msgHandlerFromStore;

    private Set<String> topics = new HashSet<>();

    private static Map<String, ObjectHandler<Object>> objectHandlerRegistry = new ConcurrentHashMap<>();
    private static Map<String, JsonHandler> jsonHandlerRegistry = new ConcurrentHashMap<>();
    private static Map<String, JsonTypeHandler> jsonTypeHandlerRegistry = new ConcurrentHashMap<>();

    private boolean seekToEndBeforeStart = false;
    private ReceivedMsgStore.StoreType storeType;

    public synchronized MsgHandler seekToEndBeforeStart(boolean seekToEndBeforeStart){
        this.seekToEndBeforeStart = seekToEndBeforeStart;
        return this;
    }

    public synchronized MsgHandler storeType(ReceivedMsgStore.StoreType storeType){
        this.storeType = storeType;
        return this;
    }

    public synchronized void start(){
        final ReceivedMsgStore receivedMsgStore = receivedMsgStore();

        this.kafkaClientConsumer.start(
                new ArrayList<>(topics),
                this.seekToEndBeforeStart,
                new MsgReceptor(
                        receivedMsgState,
                        receivedMsgStore
                )
        );
        this.msgHandlerFromStore = new MsgHandlerFromStore(objectMapper, receivedMsgState, receivedMsgStore);

        this.msgHandlerFromStore.registerJsonHandler(jsonHandlerRegistry);
        this.msgHandlerFromStore.registerJsonTypeHandler(jsonTypeHandlerRegistry);
        this.msgHandlerFromStore.registerObjectHandler(objectHandlerRegistry);

        this.msgHandlerFromStore.start();
    }

    private ReceivedMsgStore receivedMsgStore() {
        if(this.storeType == null){
            throw new IllegalArgumentException("storeType is mandatory.");
        }

        ReceivedMsgStore receivedMsgStore;
        switch (this.storeType){
            case QUEUE_STORE:
                receivedMsgStore = new QueueReceivedMsgStore();
                break;
            case REPOSITORY_STORE:
                receivedMsgStore = new RepositoryReceivedMsgStore(receivedMsgContainerRepository);
                break;
            default:
                throw new IllegalArgumentException(String.format("storeType not recognized: %s", this.storeType));
        }

        return receivedMsgStore;
    }

    public synchronized void registerJsonHandler(String topicName, String msgType, JsonHandler handler){
        if(this.kafkaClientConsumer.isStarted()){
            throw new IllegalStateException(CANNOT_REGISTER_ERROR);
        }
        if(jsonHandlerRegistry.containsKey(msgType)){
            throw new IllegalStateException(String.format("%s JsonHandler is already registered.",msgType));
        }

        topics.add(topicName);
        jsonHandlerRegistry.put(msgType, handler);
    }
    @SuppressWarnings("unchecked")
    public synchronized <T> void registerObjectHandler(String topicName, Class<?> msgType, ObjectHandler<T> handler){
        if(this.kafkaClientConsumer.isStarted()){
            throw new IllegalStateException(CANNOT_REGISTER_ERROR);
        }
        if(objectHandlerRegistry.containsKey(msgType.getCanonicalName())){
            throw new IllegalStateException(String.format("%s ObjectHandler is already registered.",msgType.getCanonicalName()));
        }

        topics.add(topicName);
        objectHandlerRegistry.put(msgType.getCanonicalName(), (ObjectHandler<Object>) handler);
    }

    public synchronized <T> void registerObjectAndJsonHandler(String topicName, Class<?> msgType, ObjectHandler<T> objectHandler, JsonHandler jsonHandler){
        registerJsonHandler(topicName,msgType.getCanonicalName(), jsonHandler);
        registerObjectHandler(topicName, msgType, objectHandler);
    }

    public synchronized <T> void registerJsonTypeHandler(String topicName, Class<?> msgType, JsonTypeHandler handler){
        if(this.kafkaClientConsumer.isStarted()){
            throw new IllegalStateException(CANNOT_REGISTER_ERROR);
        }
        if(jsonTypeHandlerRegistry.containsKey(msgType.getCanonicalName())){
            throw new IllegalStateException(String.format("%s JsonTypeHandler is already registered.",msgType.getCanonicalName()));
        }

        topics.add(topicName);
        jsonTypeHandlerRegistry.put(msgType.getCanonicalName(), handler);
    }

    public synchronized void stop(){
        this.kafkaClientConsumer.stop();
        this.msgHandlerFromStore.stop();
        jsonHandlerRegistry.clear();
        jsonTypeHandlerRegistry.clear();
        objectHandlerRegistry.clear();
    }



    }
