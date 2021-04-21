package com.demo.web.emarket.infra.async.send;

import com.demo.web.emarket.domain.ddd.ULID;
import com.demo.web.emarket.infra.async.MsgEnvelope;

import java.time.LocalDateTime;

public class SentMsgConainer implements Comparable<SentMsgConainer>{

    @Override
    public int compareTo(SentMsgConainer o) {
        if (getId() == null || o.getId() == null) {
            return 0;
        }
        return getId().compareTo(o.getId());
    }

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

    private String originMessage;

    private LocalDateTime blockTime;

    private String versionSchema;
    private String idUtilisateur;
    private String idObjet;
    private int numVersionObjet;


    public SentMsgConainer(String transactionId, String msgType, String jsonSerializedMsg, String topic, String originMessage, String versionSchema, String idUtilisateur, String idObjet, int numVersionObjet) {
       this(new ULID().nextULID(), transactionId, msgType, jsonSerializedMsg, LocalDateTime.now(), Status.WAITING_SENDING, topic, originMessage, null, versionSchema, idUtilisateur, idObjet, numVersionObjet);
    }

    public SentMsgConainer(String id, String transactionId, String msgType, String jsonSerializedMsg, LocalDateTime creationTime, Status status, String topic, String originMessage, LocalDateTime blockTime, String versionSchema, String idUtilisateur, String idObjet, int numVersionObjet) {
        this.id = id;
        this.transactionId = transactionId;
        this.msgType = msgType;
        this.jsonSerializedMsg = jsonSerializedMsg;
        this.creationTime = creationTime;
        this.status = status;
        this.topic = topic;
        this.originMessage = originMessage;
        this.blockTime = blockTime;
        this.versionSchema = versionSchema;
        this.idUtilisateur = idUtilisateur;
        this.idObjet = idObjet;
        this.numVersionObjet = numVersionObjet;
    }

    public String getVersionSchema() {
        return versionSchema;
    }

    public String getIdUtilisateur() {
        return idUtilisateur;
    }

    public String getIdObjet() {
        return idObjet;
    }

    public int getNumVersionObjet() {
        return numVersionObjet;
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

    public String getOriginMessage() {
        return originMessage;
    }

    public SentMsgConainer markAsSent(){
        this.status = Status.SENT;
        return this;
    }

    public LocalDateTime getBlockTime() {
        return blockTime;
    }

    public void blockMessage(){
        this.blockTime = LocalDateTime.now();
    }

    public MsgEnvelope asMsgEnvelope(){
        return new MsgEnvelope(id, transactionId, msgType, jsonSerializedMsg, creationTime, topic, originMessage, versionSchema, idUtilisateur, idObjet, numVersionObjet);
    }

    private SentMsgConainer(){}
}
