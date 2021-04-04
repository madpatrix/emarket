package com.demo.web.emarket.infra.async.send;

import com.demo.web.emarket.domain.UniqueId;
import com.demo.web.emarket.domain.customer.Customer;
import com.demo.web.emarket.infra.async.receive.ReceivedMsgContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface SendMsgContainerRepository extends JpaRepository<SentMsgConainer, String> {

    long countByStatus(SentMsgConainer.Status status);

    List<SentMsgConainer> findFirst1000ByStatusAndTopicOrderByCreationTimeAsc(SentMsgConainer.Status status, String topic);

    @Query("select distinct topic from SentMsgConainer")
    List<String> findTopicList();

    @Query("select distinct topic from SentMsgConainer s where s.blockTime > :date")
    List<String> findNotAvailableTopics(@Param("date") LocalDateTime date);

    List<SentMsgConainer> findFirst1000ByTopicNotInAndBlockTimeIsNullOrTopicNotInAndBlockTimeLessThanOrderByCreationTimeAsc(List<String> topics,List<String> topicsList, LocalDateTime blockTime);

    List<SentMsgConainer> findFirst1000ByBlockTimeIsNullOrBlockTimeLessThanOrderByCreationTimeAsc(LocalDateTime blockTime);


    //@Query("SELECT s FROM SentMsgConainer s WHERE (s.blockTime is null OR s.blockTime < :nowMinus5Sec) AND s.topic NOT IN (SELECT sm.topic FROM SentMsgConainer sm WHERE sm.blockTime IS NOT NULL AND sm.blockTime < :nowMinus5Sec) ORDER BY s.creation_time DESC LIMIT :limit")
    //List<SentMsgConainer> findMessagesToSend(@Param("nowMinus5Sec") LocalDateTime nowMinus5Sec, @Param("limit") int limit);

    @Modifying
    @Query("update SentMsgConainer s set s.blockTime = :blockTime where s.id = :id and s.blockTime = :actualBlockTime")
    int setMessageBlockTime(@Param("id") String id, @Param("actualBlockTime") LocalDateTime actualBlockTime, @Param("blockTime") LocalDateTime blockTime);

    @Modifying
    @Query("update SentMsgConainer s set s.blockTime = :blockTime where s.id = :id and s.blockTime is null")
   int setMessageBlockTimeWhereActualBlockTimeIsNull(@Param("id") String id, @Param("blockTime") LocalDateTime blockTime);

}
