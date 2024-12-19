package org.richardstallman.dvback.domain.ticket.repository.transaction;

import java.util.List;
import org.richardstallman.dvback.domain.ticket.domain.TicketTransactionDomain;

public interface TicketTransactionRepository {

  TicketTransactionDomain save(TicketTransactionDomain transaction);

  List<TicketTransactionDomain> findTicketsByUserId(Long userId);
}
