package com.demo.web.emarket.infra.async.receive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class ReceivedMsgState {
    private static Logger logger = LoggerFactory.getLogger(ReceivedMsgState.class);

    private AtomicLong numberOfWaitingProcessingMessages = new AtomicLong();

    public void receive(long numberOfMessages){
        this.numberOfWaitingProcessingMessages.addAndGet(numberOfMessages);
    }

    public boolean hasWaitingProcessingMessages(){
        return this.numberOfWaitingProcessingMessages.get() > 0;
    }

    public void handle(long handled){
        this.numberOfWaitingProcessingMessages.addAndGet(-handled);
        logger.trace("Handled: {}, number of waiting processing messages: {}", handled, this.numberOfWaitingProcessingMessages);
    }
}
