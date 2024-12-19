package org.richardstallman.dvback.domain.point.service;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.richardstallman.dvback.common.constant.CommonConstants.PointTransactionType;
import org.richardstallman.dvback.domain.point.converter.PointConverter;
import org.richardstallman.dvback.domain.point.converter.PointTransactionConverter;
import org.richardstallman.dvback.domain.point.domain.request.PointTransactionRequestDto;
import org.richardstallman.dvback.domain.point.domain.response.PointTransactionResponseDto;
import org.richardstallman.dvback.mock.point.FakePointRepository;
import org.richardstallman.dvback.mock.point.FakePointTransactionRepository;

@Slf4j
public class PointServiceTest {

  @InjectMocks private PointServiceImpl pointService;
  @InjectMocks private PointConverter pointConverter;
  @InjectMocks private PointTransactionConverter pointTransactionConverter;

  @BeforeEach
  void init() {
    MockitoAnnotations.openMocks(this);
    FakePointRepository fakePointRepository = new FakePointRepository();
    FakePointTransactionRepository fakePointTransactionRepository =
        new FakePointTransactionRepository();
    this.pointService =
        new PointServiceImpl(
            fakePointRepository,
            fakePointTransactionRepository,
            pointConverter,
            pointTransactionConverter);
  }

  @Test
  @DisplayName("포인트 적립 테스트")
  void deposit_point() {
    // given
    PointTransactionRequestDto pointTransactionRequestDto =
        new PointTransactionRequestDto(1L, 1000, PointTransactionType.DEPOSIT, "포인트 적립 - 이벤트 참여");

    // when
    PointTransactionResponseDto pointTransactionResponseDto =
        pointService.depositPoint(pointTransactionRequestDto);

    // then
    assertThat(pointTransactionResponseDto).isNotNull();
    assertThat(pointTransactionResponseDto.currentBalance()).isEqualTo(1000);
    assertThat(pointTransactionResponseDto.pointTransactionDetails().amount()).isEqualTo(1000);
    assertThat(pointTransactionResponseDto.pointTransactionDetails().pointTransactionType())
        .isEqualTo(PointTransactionType.DEPOSIT);
    assertThat(pointTransactionResponseDto.pointTransactionDetails().description())
        .isEqualTo("포인트 적립 - 이벤트 참여");
  }

  @Test
  @DisplayName("포인트 사용 테스트")
  void withdraw_point() {
    // given
    pointService.depositPoint(
        new PointTransactionRequestDto(1L, 1000, PointTransactionType.DEPOSIT, "포인트 적립 - 테스트"));
    PointTransactionRequestDto pointTransactionRequestDto =
        new PointTransactionRequestDto(
            1L, 1000, PointTransactionType.WITHDRAWAL, "포인트 사용 - 이용권 구입");

    // when
    PointTransactionResponseDto pointTransactionResponseDto =
        pointService.withdrawPoint(pointTransactionRequestDto);

    // then
    assertThat(pointTransactionResponseDto).isNotNull();
    assertThat(pointTransactionResponseDto.currentBalance()).isEqualTo(0);
    assertThat(pointTransactionResponseDto.pointTransactionDetails().amount()).isEqualTo(1000);
    assertThat(pointTransactionResponseDto.pointTransactionDetails().pointTransactionType())
        .isEqualTo(PointTransactionType.WITHDRAWAL);
    assertThat(pointTransactionResponseDto.pointTransactionDetails().description())
        .isEqualTo("포인트 사용 - 이용권 구입");
  }
}
