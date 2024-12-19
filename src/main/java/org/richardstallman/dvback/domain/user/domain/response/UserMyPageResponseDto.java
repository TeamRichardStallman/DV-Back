package org.richardstallman.dvback.domain.user.domain.response;

import jakarta.validation.constraints.NotNull;
import org.richardstallman.dvback.domain.ticket.domain.TicketUserCountInfoDto;

public record UserMyPageResponseDto(
    @NotNull UserResponseDto user, @NotNull TicketUserCountInfoDto ticketInfo) {}
