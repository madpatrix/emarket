package com.demo.web.emarket.infra.async.receive;

import com.demo.web.emarket.infra.async.MsgEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class MsgReceptor {
    private static Logger logger = LoggerFactory.getLogger(MsgReceptor.class);

    private ReceivedMsgState receivedMsgState;
    private ReceivedMsgStore receivedMsgStore;

    public MsgReceptor(ReceivedMsgState receivedMsgState, ReceivedMsgStore receivedMsgStore) {
        this.receivedMsgState = receivedMsgState;
        this.receivedMsgStore = receivedMsgStore;
    }

    public void receive(List<MsgEnvelope> msgEnvelopes){
        if(msgEnvelopes == null || msgEnvelopes.isEmpty()){
            return;
        }

        logger.debug("MsgReceptor receiving {} MsgEnvelope(s)", msgEnvelopes.size());
        List<ReceivedMsgContainer> receivedMsgContainers = msgEnvelopes.stream()
                .map(msgEnvelope -> new ReceivedMsgContainer(
                        msgEnvelope.getId(),
                        msgEnvelope.getMsgType(),
                        msgEnvelope.getJsonSerializedMsg(),
                        msgEnvelope.getCreationTime(),
                        msgEnvelope.getTopic()
                )).collect(Collectors.toList());

        this.receivedMsgStore.addAll(receivedMsgContainers);
        this.receivedMsgState.receive(msgEnvelopes.size());
    }
}
