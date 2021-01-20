package com.demo.web.emarket.infra.async.kafka.consumer;

import com.demo.web.emarket.infra.async.MsgEnvelope;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.Map;


public class JsonMsgEnvelopDeserializer implements Deserializer<MsgEnvelope> {
    private static Logger logger = LoggerFactory.getLogger(JsonMsgEnvelopDeserializer.class);

    private JsonDeserializer<MsgEnvelope> jsonDeserializer;

    public JsonMsgEnvelopDeserializer() {
        this.jsonDeserializer = new JsonDeserializer<MsgEnvelope>(MsgEnvelope.class, createDefaultMapper());
    }

    private ObjectMapper createDefaultMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey){
        this.jsonDeserializer.configure(configs, isKey);
    }

    @Override
    public MsgEnvelope deserialize(String topic, byte[] data) {
        try {
            return this.jsonDeserializer.deserialize(topic, data);
        }catch (Exception e){
            logger.error("Unable to deserialize message envelope", e);
        }

        return null;
    }

    @Override
    public void close() {
        this.jsonDeserializer.close();
    }
}
