package org.richardstallman.dvback.domain.ticket.repository;

import org.richardstallman.dvback.domain.ticket.entity.TicketEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketJpaRepository extends JpaRepository<TicketEntity, Long> {

  TicketEntity findByUserUserId(Long userId);
}
