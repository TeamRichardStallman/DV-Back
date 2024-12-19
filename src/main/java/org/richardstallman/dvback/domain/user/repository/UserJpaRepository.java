package org.richardstallman.dvback.domain.user.repository;

import java.util.Optional;
import org.richardstallman.dvback.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

  Optional<UserEntity> findBySocialId(String socialId);

  boolean existsByUsername(String username);

  Optional<String> findProfileImageUrlByUserId(Long userId);
}
