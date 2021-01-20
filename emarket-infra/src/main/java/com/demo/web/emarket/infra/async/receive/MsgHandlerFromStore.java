package com.demo.web.emarket.infra.async.receive;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class MsgHandlerFromStore {

    private AtomicBoolean start = new AtomicBoolean(false);
    private static AtomicLong countReceivedMsgContainer = new AtomicLong();
    private static Logger logger = LoggerFactory.getLogger(MsgHandlerFromStore.class);

    private final ObjectMapper objectMapper;
    private final ReceivedMsgState receivedMsgState;
    private final ReceivedMsgStore receivedMsgStore;

    private static Map<String, ObjectHandler<Object>> objectHandlerRegistry = new ConcurrentHashMap<>();
    private static Map<String, JsonHandler> jsonHandlerRegistry = new ConcurrentHashMap<>();
    private static Map<String, JsonTypeHandler> jsonTypeHandlerRegistry = new ConcurrentHashMap<>();

    private ScheduledExecutorService scheduledExecutorService;

    public MsgHandlerFromStore(ObjectMapper objectMapper, ReceivedMsgState receivedMsgState, ReceivedMsgStore receivedMsgStore) {
        this.objectMapper = objectMapper;
        this.receivedMsgState = receivedMsgState;
        this.receivedMsgStore = receivedMsgStore;
    }

    public void schedule(){
        if(!start.get()){
            return;
        }

        if(!receivedMsgState.hasWaitingProcessingMessages()){
            return;
        }

        logger.debug("/ Check for handling Received Msg Container Repository.");
        final List<ReceivedMsgContainer> receivedMsgContainers = this.receivedMsgStore.pollReceivedMsg();
        logger.debug("/ Handling {} Received Msg Container.", receivedMsgContainers.size());

        receivedMsgContainers.forEach(
                receivedMsgContainer -> {
                    try {
                        jsonHandler(receivedMsgContainer);
                        objectHandler(receivedMsgContainer);
                        jsonTypeHandler(receivedMsgContainer);
                    }catch (Exception e){
                        logger.error(String.format("Error while trying to handle error: $s", receivedMsgContainer.getJsonSerializedMsg()), e);
                    }finally {
                        this.receivedMsgStore.onHandled(receivedMsgContainer);
                        this.receivedMsgState.handle(1);
                    }
                }
        );

        if(!receivedMsgContainers.isEmpty()){
            countReceivedMsgContainer.addAndGet(receivedMsgContainers.size());
            logger.info("/!\\ Handled {} / all: {}", receivedMsgContainers.size(), countReceivedMsgContainer);
        }
    }

    private void jsonHandler(ReceivedMsgContainer receivedMsgContainer) {
        final String msgType = receivedMsgContainer.getMsgType();

        try{
            final JsonHandler jsonHandler = jsonHandlerRegistry.get(msgType);
            if(jsonHandler == null){
                logger.debug("No JsonHandler found for {}",msgType);
            }else{
                logger.debug("Executing JsonHandler for {}/{}", msgType, receivedMsgContainer.getId());
                jsonHandler.handle(receivedMsgContainer.getJsonSerializedMsg(), receivedMsgContainer.getId());
                logger.debug("JsonHandler was executed for {}/{}", msgType, receivedMsgContainer.getId());
            }
        }catch (Exception e){
            logger.error(String.format("Error while trying to execute JsonHandler: %s/%s", msgType, receivedMsgContainer.getId()));
        }
    }

    private void jsonTypeHandler(ReceivedMsgContainer receivedMsgContainer) {
        final String msgType = receivedMsgContainer.getMsgType();

        try{
            final JsonTypeHandler jsonTypeHandler = jsonTypeHandlerRegistry.get(msgType);
            if(jsonTypeHandler == null){
                logger.debug("No JsonTypeHandler found for {}",msgType);
            }else{
                logger.debug("Executing JsonTypeHandler for {}/{}", msgType, receivedMsgContainer.getId());
                jsonTypeHandler.handle(receivedMsgContainer.getJsonSerializedMsg(), msgType, receivedMsgContainer.getId());
                logger.debug("JsonTypeHandler was executed for {}/{}", msgType, receivedMsgContainer.getId());
            }
        }catch (Exception e){
            logger.error(String.format("Error while trying to execute jsonTypeHandler: %s/%s", msgType, receivedMsgContainer.getId()));
        }
    }

    private void objectHandler(ReceivedMsgContainer receivedMsgContainer) {
        final String msgType = receivedMsgContainer.getMsgType();

        try{
            final ObjectHandler<Object> objectHandler = objectHandlerRegistry.get(msgType);
            if(objectHandler == null){
                logger.debug("No ObjectHandler found for {}",msgType);
            }else{
               handleMessage(receivedMsgContainer, msgType, objectHandler);
            }
        }catch (Exception e){
            logger.error(String.format("Error while trying to execute ObjectHandler: %s/%s", msgType, receivedMsgContainer.getId()));
        }
    }

    private void handleMessage(ReceivedMsgContainer receivedMsgContainer, String msgType, ObjectHandler<Object> objectHandler) throws IOException {
        try {
            logger.debug("Executing ObjectHandler for {}/{}", msgType, receivedMsgContainer.getId());
            final Class<?> msgClassType = msgClassType(msgType);
            Object msg = objectMapper.readValue(receivedMsgContainer.getJsonSerializedMsg(), msgClassType);
            objectHandler.handle(msg, receivedMsgContainer.getId());
            logger.debug("ObjectHandler was executed for {}/{}", msgType, receivedMsgContainer.getId());

        }catch (ClassNotFoundException e){
            logger.error("ClassNotFound for ObjectHandler {} : {}", msgType, e);
        }
    }

    private Class<?> msgClassType(String msgType) throws ClassNotFoundException, IOException {
        Class.forName(msgType);
        return objectMapper.readValue("\""+msgType+"\"", Class.class);
    }

    public void start(){
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(
                this::schedule,
                0,
                100,
                TimeUnit.MILLISECONDS
        );
        this.start.set(true);
    }

    public void stop(){
        this.start.set(false);
        this.scheduledExecutorService.shutdown();
        jsonHandlerRegistry.clear();
        jsonTypeHandlerRegistry.clear();
        objectHandlerRegistry.clear();
    }

    public void registerObjectHandler(Map<String, ObjectHandler<Object>> objectHandlerRegistry){
        MsgHandlerFromStore.objectHandlerRegistry.putAll(objectHandlerRegistry);
    }

    public void registerJsonHandler(Map<String, JsonHandler> jsonHandlerRegistry){
        MsgHandlerFromStore.jsonHandlerRegistry.putAll(jsonHandlerRegistry);
    }

    public void registerJsonTypeHandler(Map<String, JsonTypeHandler> jsonTypeHandlerRegistry){
        MsgHandlerFromStore.jsonTypeHandlerRegistry.putAll(jsonTypeHandlerRegistry);
    }
}
