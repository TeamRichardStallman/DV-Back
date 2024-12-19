package org.richardstallman.dvback.global.jwt.refreshtoken.repository;

import java.util.Optional;
import org.richardstallman.dvback.global.jwt.refreshtoken.entity.RefreshTokenEntity;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshTokenEntity, String> {
  Optional<RefreshTokenEntity> findByRefreshToken(String refreshToken);

  void deleteByRefreshToken(String refreshToken);

  void deleteByUserId(Long userId);
}
