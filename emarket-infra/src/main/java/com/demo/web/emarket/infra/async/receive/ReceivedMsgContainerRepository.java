package com.demo.web.emarket.infra.async.receive;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface ReceivedMsgContainerRepository extends JpaRepository<ReceivedMsgContainer, String> {
   List<ReceivedMsgContainer> findFirst1000ByStatus(ReceivedMsgContainer.Status status);
}

