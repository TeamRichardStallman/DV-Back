package org.richardstallman.dvback.domain.ticket.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.common.DvApiResponse;
import org.richardstallman.dvback.domain.ticket.domain.TicketUserCountInfoDto;
import org.richardstallman.dvback.domain.ticket.domain.TicketUserInfoDto;
import org.richardstallman.dvback.domain.ticket.service.TicketService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/ticket", produces = MediaType.APPLICATION_JSON_VALUE)
public class TicketController {

  private final TicketService ticketService;

  @GetMapping("/user")
  public ResponseEntity<DvApiResponse<TicketUserInfoDto>> getUserTicketInfo(
      @AuthenticationPrincipal Long userId) {
    final TicketUserInfoDto userTicketInfo = ticketService.getUserTicketInfo(userId);

    return ResponseEntity.ok(DvApiResponse.of(userTicketInfo));
  }

  @GetMapping("/user/count")
  public ResponseEntity<DvApiResponse<TicketUserCountInfoDto>> getUserTicketCountInfo(
      @AuthenticationPrincipal Long userId) {
    final TicketUserCountInfoDto ticketUserCountInfoDto = ticketService.getUserCountInfo(userId);

    return ResponseEntity.ok(DvApiResponse.of(ticketUserCountInfoDto));
  }
}
