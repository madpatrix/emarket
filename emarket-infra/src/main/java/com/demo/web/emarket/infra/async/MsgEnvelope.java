package com.demo.web.emarket.infra.async;

import java.time.LocalDateTime;
import java.util.Objects;

public class MsgEnvelope{

    private String id;
    private String transactionId;
    private String msgType;
    private String jsonSerializedMsg;
    private LocalDateTime creationTime;
    private String topic;
    private String originMessage;
    private String versionSchema;
    private String idUtilisateur;
    private String idObjet;
    private int numVersionObjet;

    public MsgEnvelope(String id, String transactionId, String msgType, String jsonSerializedMsg, LocalDateTime creationTime, String topic, String originMessage, String versionSchema, String idUtilisateur, String idObjet, int numVersionObjet) {
        this.id = id;
        this.transactionId = transactionId;
        this.msgType = msgType;
        this.jsonSerializedMsg = jsonSerializedMsg;
        this.creationTime = creationTime;
        this.topic = topic;
        this.originMessage = originMessage;
        this.versionSchema = versionSchema;
        this.idUtilisateur = idUtilisateur;
        this.idObjet = idObjet;
        this.numVersionObjet = numVersionObjet;
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

    public String getTopic() {
        return topic;
    }

    public String getOriginMessage() {
        return originMessage;
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

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof MsgEnvelope)) return false;
        MsgEnvelope that = (MsgEnvelope) o;

        return Objects.equals(id, that.id) &&
                Objects.equals(transactionId, that.transactionId) &&
                Objects.equals(msgType, that.msgType) &&
                Objects.equals(jsonSerializedMsg, that.jsonSerializedMsg) &&
                Objects.equals(creationTime, that.creationTime) &&
                Objects.equals(topic, that.topic) &&
                Objects.equals(originMessage, that.originMessage) &&
                Objects.equals(versionSchema, that.versionSchema) &&
                Objects.equals(idUtilisateur, that.idUtilisateur) &&
                Objects.equals(idObjet, that.idObjet) &&
                numVersionObjet == that.numVersionObjet;
    }

    @Override
    public int hashCode(){
        return Objects.hash(id, transactionId, msgType, jsonSerializedMsg, creationTime, topic, originMessage, versionSchema, idUtilisateur, idObjet, numVersionObjet);
    }

    private MsgEnvelope(){}
}
