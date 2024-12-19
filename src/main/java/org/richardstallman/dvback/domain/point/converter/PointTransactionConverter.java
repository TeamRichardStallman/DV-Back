package org.richardstallman.dvback.domain.point.converter;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.point.domain.PointDomain;
import org.richardstallman.dvback.domain.point.domain.PointTransactionDomain;
import org.richardstallman.dvback.domain.point.domain.request.PointTransactionRequestDto;
import org.richardstallman.dvback.domain.point.domain.response.PointTransactionDetailResponseDto;
import org.richardstallman.dvback.domain.point.domain.response.PointTransactionResponseDto;
import org.richardstallman.dvback.domain.point.entity.PointTransactionEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointTransactionConverter {

  public PointTransactionEntity fromDomainToEntity(PointTransactionDomain pointTransactionDomain) {
    return new PointTransactionEntity(
        pointTransactionDomain.getPointTransactionId(),
        pointTransactionDomain.getUserId(),
        pointTransactionDomain.getAmount(),
        pointTransactionDomain.getPointTransactionType(),
        pointTransactionDomain.getDescription());
  }

  public PointTransactionDomain fromEntityToDomain(PointTransactionEntity pointTransactionEntity) {
    return PointTransactionDomain.builder()
        .pointTransactionId(pointTransactionEntity.getPointTransactionId())
        .userId(pointTransactionEntity.getUserId())
        .amount(pointTransactionEntity.getAmount())
        .pointTransactionType(pointTransactionEntity.getPointTransactionType())
        .description(pointTransactionEntity.getDescription())
        .build();
  }

  public PointTransactionDomain fromRequestDtoToDomain(
      PointTransactionRequestDto pointTransactionRequestDto) {
    return PointTransactionDomain.builder()
        .userId(pointTransactionRequestDto.userId())
        .amount(pointTransactionRequestDto.amount())
        .pointTransactionType(pointTransactionRequestDto.pointTransactionType())
        .description(pointTransactionRequestDto.description())
        .build();
  }

  public PointTransactionResponseDto fromDomainToResponseDto(
      PointTransactionDomain pointTransactionDomain, PointDomain pointDomain) {
    return new PointTransactionResponseDto(
        pointDomain.getBalance(),
        new PointTransactionDetailResponseDto(
            pointTransactionDomain.getPointTransactionId(),
            pointTransactionDomain.getAmount(),
            pointTransactionDomain.getPointTransactionType(),
            pointTransactionDomain.getDescription()));
  }
}
