package org.richardstallman.dvback.domain.ticket.domain;

import jakarta.validation.constraints.NotNull;

public record TicketUserCountInfoDto(
    @NotNull int totalBalance,
    @NotNull int realChatBalance,
    @NotNull int realVoiceBalance,
    @NotNull int generalChatBalance,
    @NotNull int generalVoiceBalance) {}
