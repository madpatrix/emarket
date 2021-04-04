package com.demo.web.emarket.infra.async.receive;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface ReceivedMsgContainerRepository extends JpaRepository<ReceivedMsgContainer, String> {
   List<ReceivedMsgContainer> findFirst1000ByStatus(ReceivedMsgContainer.Status status);

   Optional<ReceivedMsgContainer> findFirstByBlockTimeIsNullOrBlockTimeLessThanOrderByMsgCreationTimeDesc(LocalDateTime period);

}

