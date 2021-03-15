package com.demo.web.emarket.infra.async.send;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Repository
@Transactional
public interface SendMsgContainerRepository extends JpaRepository<SentMsgConainer, String> {

    long countByStatus(SentMsgConainer.Status status);

    @Modifying
    @Query("update SentMsgConainer s set s.blockTime = :blockTime where s.id = :id and s.blockTime = :actualBlockTime")
    int setMessageBlockTime(@Param("id") String id, @Param("actualBlockTime") LocalDateTime actualBlockTime, @Param("blockTime") LocalDateTime blockTime);

    @Modifying
    @Query("update SentMsgConainer s set s.blockTime = :blockTime where s.id = :id and s.blockTime is null")
   int setMessageBlockTimeWhereActualBlockTimeIsNull(@Param("id") String id, @Param("blockTime") LocalDateTime blockTime);

}
