package org.richardstallman.dvback.domain.ticket.service;

import org.richardstallman.dvback.domain.ticket.domain.TicketUserCountInfoDto;
import org.richardstallman.dvback.domain.ticket.domain.TicketUserInfoDto;
import org.richardstallman.dvback.domain.ticket.domain.request.TicketTransactionRequestDto;
import org.richardstallman.dvback.domain.ticket.domain.response.TicketResponseDto;
import org.richardstallman.dvback.domain.ticket.domain.response.TicketTransactionResponseDto;

public interface TicketService {

  TicketTransactionResponseDto chargeTicket(
      TicketTransactionRequestDto ticketTransactionRequestDto, Long userId);

  TicketResponseDto useTicket(TicketTransactionRequestDto ticketTransactionRequestDto, Long userId);

  TicketUserInfoDto getUserTicketInfo(Long userId);

  TicketUserCountInfoDto getUserCountInfo(Long userId);

  int getUserRealChatTicketCount(Long userId);

  int getUserRealVoiceTicketCount(Long userId);

  int getUserGeneralChatTicketCount(Long userId);

  int getUserGeneralVoiceTicketCount(Long userId);
}
