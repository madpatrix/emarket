package com.demo.web.emarket.infra.async.receive;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    public Optional<ReceivedMsgContainer> getNextMsgToConsume() {
        return receivedMsgContainerRepository.findFirstByBlockTimeIsNullOrBlockTimeLessThanOrderByMsgCreationTimeDesc(LocalDateTime.now().minusSeconds(5));
    }

    @Override
    public void addAll(List<ReceivedMsgContainer> receivedMsgContainerList) {
        this.receivedMsgContainerRepository.saveAll(receivedMsgContainerList);
    }

    @Override
    public ReceivedMsgContainer add(ReceivedMsgContainer receivedMsgContainer) {
        return this.receivedMsgContainerRepository.save(receivedMsgContainer);
    }
}
