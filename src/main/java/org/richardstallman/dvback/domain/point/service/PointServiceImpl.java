package org.richardstallman.dvback.domain.point.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.common.constant.CommonConstants.PointTransactionType;
import org.richardstallman.dvback.domain.point.converter.PointConverter;
import org.richardstallman.dvback.domain.point.converter.PointTransactionConverter;
import org.richardstallman.dvback.domain.point.domain.PointDomain;
import org.richardstallman.dvback.domain.point.domain.PointTransactionDomain;
import org.richardstallman.dvback.domain.point.domain.request.PointTransactionRequestDto;
import org.richardstallman.dvback.domain.point.domain.response.PointResponseDto;
import org.richardstallman.dvback.domain.point.domain.response.PointTransactionResponseDto;
import org.richardstallman.dvback.domain.point.repository.PointRepository;
import org.richardstallman.dvback.domain.point.repository.transaction.PointTransactionRepository;
import org.richardstallman.dvback.global.advice.exceptions.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {

  private final PointRepository pointRepository;
  private final PointTransactionRepository pointTransactionRepository;
  private final PointConverter pointConverter;
  private final PointTransactionConverter pointTransactionConverter;

  @Override
  public PointResponseDto getPointByUserId(Long userId) {
    return pointConverter.fromDomainToResponseDto(getUserPoint(userId));
  }

  private PointDomain getUserPoint(Long userId) {
    PointDomain pointDomain = pointRepository.findByUserId(userId);
    if (pointDomain == null) {
      pointDomain = pointRepository.save(PointDomain.builder().userId(userId).balance(0).build());
    }
    return pointDomain;
  }

  @Override
  @Transactional
  public PointTransactionResponseDto depositPoint(
      PointTransactionRequestDto pointTransactionRequestDto) {
    if (pointTransactionRequestDto.pointTransactionType() != PointTransactionType.DEPOSIT) {
      validateTransactionType(
          pointTransactionRequestDto.pointTransactionType(), PointTransactionType.DEPOSIT);
    }

    PointTransactionDomain pointTransactionDomain =
        pointTransactionRepository.save(
            pointTransactionConverter.fromRequestDtoToDomain(pointTransactionRequestDto));

    PointDomain pointDomain =
        pointRepository.save(
            pointConverter.updateBalance(
                getUserPoint(pointTransactionRequestDto.userId()),
                pointTransactionRequestDto.amount()));

    return pointTransactionConverter.fromDomainToResponseDto(pointTransactionDomain, pointDomain);
  }

  @Override
  @Transactional
  public PointTransactionResponseDto withdrawPoint(
      PointTransactionRequestDto pointTransactionRequestDto) {
    if (pointTransactionRequestDto.pointTransactionType() != PointTransactionType.WITHDRAWAL) {
      validateTransactionType(
          pointTransactionRequestDto.pointTransactionType(), PointTransactionType.WITHDRAWAL);
    }
    PointDomain pointDomain = getUserPoint(pointTransactionRequestDto.userId());
    if (pointTransactionRequestDto.amount() > pointDomain.getBalance()) {
      throw new IllegalArgumentException(
          "Amount less than balance. user id : ("
              + pointTransactionRequestDto.userId()
              + "), current balance: ("
              + pointDomain.getBalance()
              + ")");
    }

    PointTransactionDomain pointTransactionDomain =
        pointTransactionRepository.save(
            pointTransactionConverter.fromRequestDtoToDomain(pointTransactionRequestDto));

    pointDomain =
        pointRepository.save(
            pointConverter.updateBalance(
                getUserPoint(pointTransactionRequestDto.userId()),
                pointTransactionRequestDto.amount() * -1));

    return pointTransactionConverter.fromDomainToResponseDto(pointTransactionDomain, pointDomain);
  }

  private void validateTransactionType(PointTransactionType actual, PointTransactionType expected) {
    if (actual != expected) {
      throw new ApiException(
          HttpStatus.BAD_REQUEST,
          "Invalid transaction type: expected (" + expected + ") but got (" + actual + ")");
    }
  }
}
