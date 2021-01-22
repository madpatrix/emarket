package com.demo.web.emarket.infra.async.send;

import com.demo.web.emarket.domain.UniqueId;
import com.demo.web.emarket.domain.customer.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface SendMsgContainerRepository extends JpaRepository<SentMsgConainer, String> {

    long countByStatus(SentMsgConainer.Status status);

    List<SentMsgConainer> findFirst1000ByStatusAndTopicOrderByCreationTimeAsc(SentMsgConainer.Status status, String topic);

    @Query("select distinct topic from SentMsgConainer")
    List<String> findTopicList();
}
