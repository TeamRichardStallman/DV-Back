package org.richardstallman.dvback.domain.ticket.domain.response;

import jakarta.validation.constraints.NotNull;

public record TicketResponseDto(
    @NotNull int totalBalance,
    @NotNull int realChatBalance,
    @NotNull int realVoiceBalance,
    @NotNull int generalChatBalance,
    @NotNull int generalVoiceBalance,
    @NotNull TicketTransactionDetailResponseDto ticketTransactionDetail) {}
