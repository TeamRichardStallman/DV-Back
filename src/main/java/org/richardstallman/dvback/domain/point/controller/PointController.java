package org.richardstallman.dvback.domain.point.controller;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.common.DvApiResponse;
import org.richardstallman.dvback.domain.point.domain.response.PointResponseDto;
import org.richardstallman.dvback.domain.point.service.PointService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/point")
public class PointController {

  private final PointService pointService;

  @GetMapping("/user/{userId}")
  public ResponseEntity<DvApiResponse<PointResponseDto>> getPointByUserId(
      @PathVariable Long userId // 유저 구현되면 요청 정보에서 가져와서 사용
      ) {
    final PointResponseDto pointResponseDto = pointService.getPointByUserId(userId);
    return ResponseEntity.ok(DvApiResponse.of(pointResponseDto));
  }
}
