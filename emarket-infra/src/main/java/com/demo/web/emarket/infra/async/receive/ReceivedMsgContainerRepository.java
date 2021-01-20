package com.demo.web.emarket.infra.async.receive;

import com.demo.web.emarket.infra.async.send.SentMsgConainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReceivedMsgContainerRepository {

    @Autowired
    private JdbcReceivedMsgContainerRepository jdbcReceivedMsgContainerRepository;

    public List<ReceivedMsgContainer> findAll(){ return this.jdbcReceivedMsgContainerRepository.findAll();}

    public void save(List<ReceivedMsgContainer> receivedMsgContainers){
        this.jdbcReceivedMsgContainerRepository.save(receivedMsgContainers);
    }

    public List<ReceivedMsgContainer> findFirst1000ByStatus(ReceivedMsgContainer.Status status){
        return this.jdbcReceivedMsgContainerRepository.findFirst1000ByStatus(status);
    }

    public void updateStatus(ReceivedMsgContainer receivedMsgContainer){
        this.jdbcReceivedMsgContainerRepository.updateStatus(receivedMsgContainer);
    }

    public void delete(ReceivedMsgContainer receivedMsgContainer){ this.jdbcReceivedMsgContainerRepository.delete(receivedMsgContainer);}
}
