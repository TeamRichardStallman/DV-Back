package org.richardstallman.dvback.domain.ticket.repository;

import org.richardstallman.dvback.domain.ticket.domain.TicketDomain;

public interface TicketRepository {

  TicketDomain save(TicketDomain ticketDomain);

  TicketDomain findByUserId(Long userId);
}
