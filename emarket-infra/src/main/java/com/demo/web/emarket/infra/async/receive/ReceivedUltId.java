package com.demo.web.emarket.infra.async.receive;

import java.io.Serializable;

public class ReceivedUltId implements Serializable {
    private String topic;

    private int partition;


    public ReceivedUltId() {
    }

    public ReceivedUltId(String topic, int partition) {
        this.topic = topic;
        this.partition = partition;

    }

    public String getTopic() {
        return topic;
    }

    public int getPartition() {
        return partition;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }
    public int hashCode() { return topic.hashCode() + partition; }
    public boolean equals(Object obj) {
        try {
            if (this == obj) return true;
            return topic.equals(((ReceivedUltId) obj).getTopic()) &&
                    partition == (((ReceivedUltId) obj).getPartition());

        } catch (Throwable ignored) {
            return false;
        }
    }

}
