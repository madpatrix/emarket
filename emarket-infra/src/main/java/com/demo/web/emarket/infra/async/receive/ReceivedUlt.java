package com.demo.web.emarket.infra.async.receive;


import java.time.LocalDateTime;

public class ReceivedUlt {

    private String ulid;
    private ReceivedUltId id;
    private int retriesNo;
    private LocalDateTime ts;
    private long msgOffset;


    public ReceivedUlt(String ulid, ReceivedUltId id, long msgOffset) {
        this.ulid = ulid;
        this.id = id;
        this.msgOffset = msgOffset;
        this.retriesNo = 0;
        this.ts = LocalDateTime.now();
    }

    public String getUlid() {
        return ulid;
    }

    public ReceivedUltId getId() {
        return id;
    }

    public void setId(ReceivedUltId id) {
        this.id = id;
    }

    public void setUlid(String ulid) {
        this.ulid = ulid;
    }

    public long getMsgOffset() {
        return msgOffset;
    }

    public void setMsgOffset(long msgOffset) {
        this.msgOffset = msgOffset;
    }

    public void setRetriesNo(int retriesNo) {
        this.retriesNo = retriesNo;
    }

    public int getRetriesNo() {
        return retriesNo;
    }

    public void incrementRetriesNo() {
        this.retriesNo+= 1;
        this.ts = LocalDateTime.now();
    }

    public void setTs(LocalDateTime ts) {
        this.ts = ts;
    }

    public LocalDateTime getTs() {
        return ts;
    }

    public void resetRetriesNo() {
        this.retriesNo = 0;
        this.ts = LocalDateTime.now();
    }

    private ReceivedUlt() {
    }
}
