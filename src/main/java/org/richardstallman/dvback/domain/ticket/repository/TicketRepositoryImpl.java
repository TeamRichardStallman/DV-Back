package org.richardstallman.dvback.domain.ticket.repository;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.ticket.converter.TicketConverter;
import org.richardstallman.dvback.domain.ticket.domain.TicketDomain;
import org.richardstallman.dvback.domain.ticket.entity.TicketEntity;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TicketRepositoryImpl implements TicketRepository {

  private final TicketConverter ticketConverter;
  private final TicketJpaRepository ticketJpaRepository;

  @Override
  public TicketDomain save(TicketDomain ticketDomain) {
    return ticketConverter.fromEntityToDomain(
        ticketJpaRepository.save(ticketConverter.fromDomainToEntity(ticketDomain)));
  }

  @Override
  public TicketDomain findByUserId(Long userId) {
    TicketEntity ticketEntity = ticketJpaRepository.findByUserUserId(userId);
    if (ticketEntity == null) {
      return null;
    }
    return ticketConverter.fromEntityToDomain(ticketEntity);
  }
}
