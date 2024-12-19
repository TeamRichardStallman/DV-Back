package org.richardstallman.dvback.domain.ticket.domain.request;

import jakarta.validation.constraints.NotNull;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewAssetType;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.common.constant.CommonConstants.TicketTransactionMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.TicketTransactionType;

public record TicketTransactionRequestDto(
    @NotNull int amount,
    @NotNull TicketTransactionType ticketTransactionType,
    @NotNull TicketTransactionMethod ticketTransactionMethod,
    @NotNull InterviewMode interviewMode,
    @NotNull InterviewAssetType interviewAssetType,
    @NotNull String description) {}
