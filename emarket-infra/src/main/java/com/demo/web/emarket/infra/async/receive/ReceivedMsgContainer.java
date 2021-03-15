package com.demo.web.emarket.infra.async.receive;

import org.apache.kafka.common.protocol.types.Field;

import java.time.LocalDateTime;

public class ReceivedMsgContainer {
    public enum Status{
        WAITING_PROCESSING,
        PROCESSING_STARTED,
        PROCESSING_DONE
    }

    private String id;
    private String msgType;
    private String jsonSerializedMsg;
    private LocalDateTime msgCreationTime;
    private LocalDateTime receivedTime;
    private Status status;
    private String topic;
    private String originMessage;
    private LocalDateTime blockTime;

    public ReceivedMsgContainer(String id, String msgType, String jsonSerializedMsg, LocalDateTime msgCreationTime, LocalDateTime receivedTime, Status status, String topic, String originMessage, LocalDateTime blockTime) {
        this.id = id;
        this.msgType = msgType;
        this.jsonSerializedMsg = jsonSerializedMsg;
        this.msgCreationTime = msgCreationTime;
        this.receivedTime = receivedTime;
        this.status = status;
        this.topic = topic;
        this.originMessage = originMessage;
        this.blockTime = blockTime;
    }

    public ReceivedMsgContainer(String id, String msgType, String jsonSerializedMsg, LocalDateTime msgCreationTime, String topic, String originMessage) {
       this(id, msgType, jsonSerializedMsg, msgCreationTime, LocalDateTime.now(), Status.WAITING_PROCESSING, topic, originMessage, null);
    }

    public String getId() {
        return id;
    }

    public String getMsgType() {
        return msgType;
    }

    public String getJsonSerializedMsg() {
        return jsonSerializedMsg;
    }

    public LocalDateTime getMsgCreationTime() {
        return msgCreationTime;
    }

    public LocalDateTime getReceivedTime() {
        return receivedTime;
    }

    public Status getStatus() {
        return status;
    }

    public String getTopic() {
        return topic;
    }

    public String getOriginMessage() {
        return originMessage;
    }

    public LocalDateTime getBlockTime() {
        return blockTime;
    }

    public void blockMessage(){
        this.blockTime = LocalDateTime.now();
    }

    private ReceivedMsgContainer() {
    }
}
