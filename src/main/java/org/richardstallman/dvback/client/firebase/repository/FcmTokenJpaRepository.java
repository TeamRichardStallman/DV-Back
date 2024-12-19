package org.richardstallman.dvback.client.firebase.repository;

import org.richardstallman.dvback.client.firebase.entity.FcmTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmTokenJpaRepository extends JpaRepository<FcmTokenEntity, Long> {

  FcmTokenEntity findByUserUserId(Long userId);

  void deleteByUserUserId(Long userId);
}
