package org.richardstallman.dvback.domain.point.service;

import org.richardstallman.dvback.domain.point.domain.request.PointTransactionRequestDto;
import org.richardstallman.dvback.domain.point.domain.response.PointResponseDto;
import org.richardstallman.dvback.domain.point.domain.response.PointTransactionResponseDto;

public interface PointService {

  PointResponseDto getPointByUserId(Long userId);

  PointTransactionResponseDto depositPoint(PointTransactionRequestDto pointTransactionRequestDto);

  PointTransactionResponseDto withdrawPoint(PointTransactionRequestDto pointTransactionRequestDto);
}
