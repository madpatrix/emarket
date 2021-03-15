package com.demo.web.emarket.infra.async.receive;

import java.util.List;
import java.util.Optional;

public interface ReceivedMsgStore {

    enum StoreType{
        REPOSITORY_STORE,
        QUEUE_STORE
    }

    void onHandled(ReceivedMsgContainer receivedMsgContainer);

    List<ReceivedMsgContainer> pollReceivedMsg();

    Optional<ReceivedMsgContainer> getNextMsgToConsume();

    void addAll(List<ReceivedMsgContainer> receivedMsgContainerList);

    ReceivedMsgContainer add(ReceivedMsgContainer receivedMsgContainer);
}
