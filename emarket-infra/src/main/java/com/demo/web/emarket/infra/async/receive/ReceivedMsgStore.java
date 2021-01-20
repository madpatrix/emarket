package com.demo.web.emarket.infra.async.receive;

import java.util.List;

public interface ReceivedMsgStore {

    enum StoreType{
        REPOSITORY_STORE,
        QUEUE_STORE
    }

    void onHandled(ReceivedMsgContainer receivedMsgContainer);

    List<ReceivedMsgContainer> pollReceivedMsg();

    void addAll(List<ReceivedMsgContainer> receivedMsgContainerList);
}
