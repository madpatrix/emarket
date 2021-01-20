package com.demo.web.emarket.infra.async.kafka.stub;

import com.demo.web.emarket.infra.async.MsgEnvelope;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.serialization.Serializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

public class JsonMsgEnvelopSerializer implements Serializer<MsgEnvelope> {

    private JsonSerializer<MsgEnvelope> jsonSerializer;

    public JsonMsgEnvelopSerializer() {
        this.jsonSerializer = new JsonSerializer<>(createDefaultMapper());
    }

    private ObjectMapper createDefaultMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        return objectMapper;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        this.jsonSerializer.configure(configs, isKey);
    }

    @Override
    public byte[] serialize(String topic, MsgEnvelope data) {
        return this.jsonSerializer.serialize(topic, data);
    }

    @Override
    public void close() {
        this.jsonSerializer.close();
    }
}
