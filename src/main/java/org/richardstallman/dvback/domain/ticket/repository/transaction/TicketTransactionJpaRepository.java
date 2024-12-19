package org.richardstallman.dvback.domain.ticket.repository.transaction;

import java.util.List;
import org.richardstallman.dvback.common.constant.CommonConstants.TicketTransactionType;
import org.richardstallman.dvback.domain.ticket.entity.TicketTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketTransactionJpaRepository
    extends JpaRepository<TicketTransactionEntity, Long> {

  List<TicketTransactionEntity> findByUserUserIdOrderByTicketTransactionIdDesc(Long userId);

  List<TicketTransactionEntity> findByUserUserIdAndTicketTransactionType(
      Long userId, TicketTransactionType ticketTransactionType);
}
