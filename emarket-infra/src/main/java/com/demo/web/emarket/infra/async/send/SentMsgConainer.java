package com.demo.web.emarket.infra.async.send;

import com.demo.web.emarket.infra.async.MsgEnvelope;

import java.time.LocalDateTime;
import java.util.UUID;

public class SentMsgConainer {

    public enum Status{
        WAITING_SENDING,
        SENDING_STARTED,
        SENT
    }

    private String id;
    private String transactionId;
    private String msgType;
    private String jsonSerializedMsg;

    private LocalDateTime creationTime;
    private Status status;
    private String topic;


    public SentMsgConainer(String transactionId, String msgType, String jsonSerializedMsg, String topic) {
       this(UUID.randomUUID().toString(), transactionId, msgType, jsonSerializedMsg, LocalDateTime.now(), Status.WAITING_SENDING, topic);
    }

    public SentMsgConainer(String id, String transactionId, String msgType, String jsonSerializedMsg, LocalDateTime creationTime, Status status, String topic) {
        this.id = id;
        this.transactionId = transactionId;
        this.msgType = msgType;
        this.jsonSerializedMsg = jsonSerializedMsg;
        this.creationTime = creationTime;
        this.status = status;
        this.topic = topic;
    }

    public String getId() {
        return id;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getMsgType() {
        return msgType;
    }

    public String getJsonSerializedMsg() {
        return jsonSerializedMsg;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public Status getStatus() {
        return status;
    }

    public String getTopic() {
        return topic;
    }

    public SentMsgConainer markAsSent(){
        this.status = Status.SENT;
        return this;
    }

    public MsgEnvelope asMsgEnvelope(){
        return new MsgEnvelope(id, transactionId, msgType, jsonSerializedMsg, creationTime, topic);
    }

    private SentMsgConainer(){}
}
