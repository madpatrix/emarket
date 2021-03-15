package com.demo.web.emarket.infra.async.receive;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class QueueReceivedMsgStore implements ReceivedMsgStore{

    private final BlockingQueue<ReceivedMsgContainer> receivedMsgQueue = new LinkedBlockingQueue<>();

    @Override
    public void onHandled(ReceivedMsgContainer receivedMsgContainer) {
        // this receiver has do to nothing on this event
    }

    @Override
    public List<ReceivedMsgContainer> pollReceivedMsg() {
        try {
            return Collections.singletonList(this.receivedMsgQueue.take());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ReceivedMsgContainer> getNextMsgToConsume() {
       //nothing to do;
        return null;
    }

    @Override
    public void addAll(List<ReceivedMsgContainer> receivedMsgContainerList) {
        this.receivedMsgQueue.addAll(receivedMsgContainerList);
    }

    @Override
    public ReceivedMsgContainer add(ReceivedMsgContainer receivedMsgContainer) {
        this.receivedMsgQueue.add(receivedMsgContainer);
        return receivedMsgContainer;
    }
}
