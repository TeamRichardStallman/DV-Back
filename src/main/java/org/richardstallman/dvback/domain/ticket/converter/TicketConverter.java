package org.richardstallman.dvback.domain.ticket.converter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewAssetType;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.domain.ticket.domain.TicketDomain;
import org.richardstallman.dvback.domain.ticket.domain.TicketUserCountInfoDto;
import org.richardstallman.dvback.domain.ticket.domain.TicketUserInfoDto;
import org.richardstallman.dvback.domain.ticket.domain.response.TicketTransactionDetailResponseDto;
import org.richardstallman.dvback.domain.ticket.entity.TicketEntity;
import org.richardstallman.dvback.domain.user.converter.UserConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketConverter {

  private final UserConverter userConverter;

  public TicketDomain fromEntityToDomain(TicketEntity ticketEntity) {
    return TicketDomain.builder()
        .ticketId(ticketEntity.getTicketId())
        .userDomain(userConverter.fromEntityToDomain(ticketEntity.getUser()))
        .realChatBalance(ticketEntity.getRealChatBalance())
        .realVoiceBalance(ticketEntity.getRealVoiceBalance())
        .generalChatBalance(ticketEntity.getGeneralChatBalance())
        .generalVoiceBalance(ticketEntity.getGeneralVoiceBalance())
        .build();
  }

  public TicketEntity fromDomainToEntity(TicketDomain ticketDomain) {
    return new TicketEntity(
        ticketDomain.getTicketId(),
        userConverter.fromDomainToEntity(ticketDomain.getUserDomain()),
        ticketDomain.getRealChatBalance(),
        ticketDomain.getRealVoiceBalance(),
        ticketDomain.getGeneralChatBalance(),
        ticketDomain.getGeneralVoiceBalance());
  }

  public TicketDomain updateBalance(
      TicketDomain ticketDomain,
      int amount,
      InterviewAssetType interviewAssetType,
      InterviewMode interviewMode) {
    return TicketDomain.builder()
        .ticketId(ticketDomain.getTicketId())
        .userDomain(ticketDomain.getUserDomain())
        .realChatBalance(
            interviewMode == InterviewMode.GENERAL
                ? ticketDomain.getRealChatBalance()
                : interviewAssetType == InterviewAssetType.CHAT
                    ? ticketDomain.getRealChatBalance() + amount
                    : ticketDomain.getRealChatBalance())
        .realVoiceBalance(
            interviewMode == InterviewMode.GENERAL
                ? ticketDomain.getRealVoiceBalance()
                : interviewAssetType == InterviewAssetType.VOICE
                    ? ticketDomain.getRealVoiceBalance() + amount
                    : ticketDomain.getRealVoiceBalance())
        .generalChatBalance(
            interviewMode == InterviewMode.REAL
                ? ticketDomain.getGeneralChatBalance()
                : interviewAssetType == InterviewAssetType.CHAT
                    ? ticketDomain.getGeneralChatBalance() + amount
                    : ticketDomain.getGeneralChatBalance())
        .generalVoiceBalance(
            interviewMode == InterviewMode.REAL
                ? ticketDomain.getGeneralVoiceBalance()
                : interviewAssetType == InterviewAssetType.VOICE
                    ? ticketDomain.getGeneralVoiceBalance() + amount
                    : ticketDomain.getGeneralVoiceBalance())
        .build();
  }

  public TicketUserInfoDto generateUserInfoDto(
      TicketUserCountInfoDto ticketUserCountInfoDto,
      List<TicketTransactionDetailResponseDto> ticketTransactionDetailResponseDtos) {
    return new TicketUserInfoDto(ticketUserCountInfoDto, ticketTransactionDetailResponseDtos);
  }
}
