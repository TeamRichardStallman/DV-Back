package org.richardstallman.dvback.domain.point.converter;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.point.domain.PointDomain;
import org.richardstallman.dvback.domain.point.domain.response.PointResponseDto;
import org.richardstallman.dvback.domain.point.entity.PointEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PointConverter {

  public PointEntity fromDomainToEntity(PointDomain pointDomain) {
    return new PointEntity(
        pointDomain.getPointId(), pointDomain.getUserId(), pointDomain.getBalance());
  }

  public PointDomain fromEntityToDomain(PointEntity pointEntity) {
    return PointDomain.builder()
        .pointId(pointEntity.getPointId())
        .userId(pointEntity.getUserId())
        .balance(pointEntity.getBalance())
        .build();
  }

  public PointResponseDto fromDomainToResponseDto(PointDomain pointDomain) {
    return new PointResponseDto(pointDomain.getBalance());
  }

  public PointDomain updateBalance(PointDomain pointDomain, int amount) {
    return PointDomain.builder()
        .pointId(pointDomain.getPointId())
        .userId(pointDomain.getUserId())
        .balance(pointDomain.getBalance() + amount)
        .build();
  }
}
