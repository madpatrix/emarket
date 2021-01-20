package com.demo.web.emarket.infra.async.send;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class SendMsgContainerRepository {

    @Autowired private JdbcSendMsgContainerRepository jdbcSendMsgContainerRepository;

    public List<SentMsgConainer> findAll(){
        return this.jdbcSendMsgContainerRepository.findAll();
    }

    public void save(SentMsgConainer sentMsgConainer){
        this.jdbcSendMsgContainerRepository.save(sentMsgConainer);
    }

    public void save(List<SentMsgConainer> sentMsgConainers){
        this.jdbcSendMsgContainerRepository.save(sentMsgConainers);
    }

    public void delete(String id){ this.jdbcSendMsgContainerRepository.delete(id);}

    public long countByStatus(SentMsgConainer.Status status){ return this.jdbcSendMsgContainerRepository.countByStatus(status);}

    public List<String> findTopicList(){ return this.jdbcSendMsgContainerRepository.findTopicList();}

    public List<SentMsgConainer> findFirst1000ByStatusAndTopic(SentMsgConainer.Status status, String topic){
        return this.jdbcSendMsgContainerRepository.findFirst1000ByStatusAndTopic(status, topic);
    }

}
