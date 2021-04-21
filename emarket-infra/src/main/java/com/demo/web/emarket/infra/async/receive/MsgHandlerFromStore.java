package com.demo.web.emarket.infra.async.receive;

import com.demo.web.emarket.infra.async.MsgEnvelope;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MsgHandlerFromStore {

    private static Logger logger = LoggerFactory.getLogger(MsgHandlerFromStore.class);

    private final ObjectMapper objectMapper;

    private static Map<String, ObjectHandler<Object>> objectHandlerRegistry = new ConcurrentHashMap<>();
    private static Map<String, JsonHandler> jsonHandlerRegistry = new ConcurrentHashMap<>();
    private static Map<String, JsonTypeHandler> jsonTypeHandlerRegistry = new ConcurrentHashMap<>();

    public MsgHandlerFromStore(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    public void schedule(MsgEnvelope msgEnvelope){

        try {
            jsonHandler(msgEnvelope);
            objectHandler(msgEnvelope);
            jsonTypeHandler(msgEnvelope);
       }catch (Exception e){
            logger.error(String.format("Error while trying to handle error: $s", msgEnvelope.getJsonSerializedMsg()), e);
            throw e;
        }

    }

    private void jsonHandler(MsgEnvelope msgEnvelope) {
        final String msgType = msgEnvelope.getMsgType();

        try{
            final JsonHandler jsonHandler = jsonHandlerRegistry.get(msgType);
            if(jsonHandler == null){
                logger.debug("No JsonHandler found for {}",msgType);
            }else{
                logger.debug("Executing JsonHandler for {}/{}", msgType, msgEnvelope.getId());
                jsonHandler.handle(msgEnvelope.getJsonSerializedMsg(), msgEnvelope.getId());
                logger.debug("JsonHandler was executed for {}/{}", msgType, msgEnvelope.getId());
            }
        }catch (Exception e){
            logger.error(String.format("Error while trying to execute JsonHandler: %s/%s", msgType, msgEnvelope.getId()));
            throw e;
        }
    }

    private void jsonTypeHandler(MsgEnvelope msgEnvelope) {
        final String msgType = msgEnvelope.getMsgType();

        try{
            final JsonTypeHandler jsonTypeHandler = jsonTypeHandlerRegistry.get(msgType);
            if(jsonTypeHandler == null){
                logger.debug("No JsonTypeHandler found for {}",msgType);
            }else{
                logger.debug("Executing JsonTypeHandler for {}/{}", msgType, msgEnvelope.getId());
                jsonTypeHandler.handle(msgEnvelope.getJsonSerializedMsg(), msgType, msgEnvelope.getId());
                logger.debug("JsonTypeHandler was executed for {}/{}", msgType, msgEnvelope.getId());
            }
        }catch (Exception e){
            logger.error(String.format("Error while trying to execute jsonTypeHandler: %s/%s", msgType, msgEnvelope.getId()));
        }
    }

    private void objectHandler(MsgEnvelope msgEnvelope) {
        final String msgType = msgEnvelope.getMsgType();

        try{
            final ObjectHandler<Object> objectHandler = objectHandlerRegistry.get(msgType);
            if(objectHandler == null){
                logger.debug("No ObjectHandler found for {}",msgType);
            }else{
               handleMessage(msgEnvelope, msgType, objectHandler);
            }
        }catch (Exception e){
            logger.error(String.format("Error while trying to execute ObjectHandler: %s/%s", msgType, msgEnvelope.getId()));
        }
    }

    private void handleMessage(MsgEnvelope msgEnvelope, String msgType, ObjectHandler<Object> objectHandler) throws IOException {
        try {
            logger.debug("Executing ObjectHandler for {}/{}", msgType, msgEnvelope.getId());
            final Class<?> msgClassType = msgClassType(msgType);
            Object msg = objectMapper.readValue(msgEnvelope.getJsonSerializedMsg(), msgClassType);
            objectHandler.handle(msg, msgEnvelope.getId());
            logger.debug("ObjectHandler was executed for {}/{}", msgType, msgEnvelope.getId());

        }catch (ClassNotFoundException e){
            logger.error("ClassNotFound for ObjectHandler {} : {}", msgType, e);
        }
    }

    private Class<?> msgClassType(String msgType) throws ClassNotFoundException, IOException {
        Class.forName(msgType);
        return objectMapper.readValue("\""+msgType+"\"", Class.class);
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
