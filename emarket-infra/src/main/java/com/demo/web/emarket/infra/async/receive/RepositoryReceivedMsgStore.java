package com.demo.web.emarket.infra.async.receive;

import java.util.List;

public class RepositoryReceivedMsgStore implements ReceivedMsgStore{

    private final ReceivedMsgContainerRepository receivedMsgContainerRepository;

    public RepositoryReceivedMsgStore(ReceivedMsgContainerRepository receivedMsgContainerRepository) {
        this.receivedMsgContainerRepository = receivedMsgContainerRepository;
    }

    @Override
    public void onHandled(ReceivedMsgContainer receivedMsgContainer) {
        this.receivedMsgContainerRepository.delete(receivedMsgContainer);
    }

    @Override
    public List<ReceivedMsgContainer> pollReceivedMsg() {
        return this.receivedMsgContainerRepository.findFirst1000ByStatus(ReceivedMsgContainer.Status.WAITING_PROCESSING);
    }

    @Override
    public void addAll(List<ReceivedMsgContainer> receivedMsgContainerList) {
        this.receivedMsgContainerRepository.save(receivedMsgContainerList);
    }
}
