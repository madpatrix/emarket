package com.demo.web.emarket.infra.async.send;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class EmitterInRepository {
    private Logger logger = LoggerFactory.getLogger(EmitterInRepository.class);

    private static AtomicLong countSentMsgContainerSaved = new AtomicLong();
    private SendMsgContainerRepository sendMsgContainerRepository;
    @Autowired
    private ObjectMapper objectMapper;
    private SentMsgState sentMsgState;
    @Value("${kafka.origin.message}")
    private String originMessage;

    public EmitterInRepository(SendMsgContainerRepository sendMsgContainerRepository, SentMsgState sentMsgState) {
        this.sendMsgContainerRepository = sendMsgContainerRepository;
        this.sentMsgState = sentMsgState;
    }


    public LocalDateTime emit(Object msg, String transactionId, String topic){
        try {
            final String json = this.objectMapper.writeValueAsString(msg);
            String msgType = msg.getClass().getCanonicalName();
            SentMsgConainer sentMsgConainer = new SentMsgConainer(transactionId, msgType, json, topic, originMessage);
            this.sendMsgContainerRepository.save(sentMsgConainer);
            this.sentMsgState.emit(1);

            return sentMsgConainer.getCreationTime();

        }catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }
}
