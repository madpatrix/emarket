package com.demo.web.emarket.infra.async.send;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class SentMsgState {
    private static final Logger logger = LoggerFactory.getLogger(SentMsgState.class);
    public static final int MAX_SUCCESSIVE_SEND_ERROR_1_H = 100;
    public static final int MAX_SUCCESSIVE_SEND_ERROR_1_M = 10;

    private ConcurrentHashMap<String, Boolean> sendingErrorTopicMap = new ConcurrentHashMap<>();

    private AtomicLong numberOfWaitingSendingMessages = new AtomicLong();
    private AtomicLong succesiveEmptyReadFromRepository = new AtomicLong();

    private AtomicLong successiveSendError = new AtomicLong();

    public synchronized void emit(long numberOfMessages){
        this.numberOfWaitingSendingMessages.addAndGet(numberOfMessages);
        logger.trace("emit: {} => numberOfWaitingSendingMessages: {}", numberOfMessages, this.numberOfWaitingSendingMessages);
    }

    public synchronized boolean hasWaitingSendingMessages(){
        boolean hasWaitingSendingMessages = this.numberOfWaitingSendingMessages.get() > 0;
        logger.trace("hasWaitingSendingMessages: {} => numberOfWaitingSendingMessages: {}", hasWaitingSendingMessages, this.numberOfWaitingSendingMessages);
        return hasWaitingSendingMessages;
    }

    public synchronized void sent(long sent){
        this.numberOfWaitingSendingMessages.addAndGet(-sent);
        if(this.numberOfWaitingSendingMessages.get()<0){
            this.numberOfWaitingSendingMessages.set(0);
        }

        logger.trace("sent: {} => numberOfWaitingSendingMessages: {}", sent, this.numberOfWaitingSendingMessages);

    }

    public synchronized void reset(){
        if(this.numberOfWaitingSendingMessages.get() > 0){
            logger.trace("Reset from {} to {}", this.numberOfWaitingSendingMessages, 0);
            this.numberOfWaitingSendingMessages.set(0);
        }
    }

    public long incSuccessiveEmptyReadFromRepository(){
        return succesiveEmptyReadFromRepository.incrementAndGet();
    }

    public long getSuccessiveEmptyReadFromRepository(){
        return succesiveEmptyReadFromRepository.get();
    }

    public synchronized void checkReadFromRepository(int size){
        if(size == 0){
            incSuccessiveEmptyReadFromRepository();
        }else if(size > 0){
            this.succesiveEmptyReadFromRepository.set(0);
        }else {
            throw new IllegalArgumentException("size cannot be < 0");
        }
        if(this.succesiveEmptyReadFromRepository.get()>1){
            reset();
        }
    }

    public long incSuccessiveSendError(){
        return successiveSendError.incrementAndGet();
    }

    public void resetSuccessiveSendError(){
        successiveSendError.set(0);
    }

    public long getSuccessiveSendError(){
        return successiveSendError.get();
    }

    public boolean isSuccessiveSendErrorGreaterThan(int maxSuccessiveSendError){
        return this.successiveSendError.get() > maxSuccessiveSendError;
    }

    private long getSleepDurationBecauseOfSuccessiveSendError(){
        if(this.successiveSendError.get() > MAX_SUCCESSIVE_SEND_ERROR_1_H){
            return Duration.ofHours(1).toMillis();
        }else if(this.successiveSendError.get() > MAX_SUCCESSIVE_SEND_ERROR_1_M){
            return Duration.ofMinutes(1).toMillis();
        }else{
            return 0;
        }
    }

    public void trySleepBecauseOfSuccessiveSendError() throws InterruptedException {
        if(getSleepDurationBecauseOfSuccessiveSendError() > 0){
            logger.warn("SuccessiveSendError: {}. Sleep {} ms", this.successiveSendError.get(), getSleepDurationBecauseOfSuccessiveSendError());
            try {
                Thread.sleep(getSleepDurationBecauseOfSuccessiveSendError());
            } catch (InterruptedException e) {
                logger.info("", e);
                throw e;
            }
        }
    }

    public void setSendingErrorForTopic(String topic){
        this.sendingErrorTopicMap.put(topic, true);
    }

    public boolean isSendingErrorForTopic(String topic){
        return this.sendingErrorTopicMap.getOrDefault(topic, false);
    }

    public void resetSendingErrorForAllTopics(){
        this.sendingErrorTopicMap.clear();
    }
}
