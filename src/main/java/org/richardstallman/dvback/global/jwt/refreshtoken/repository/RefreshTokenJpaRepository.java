package org.richardstallman.dvback.global.jwt.refreshtoken.repository;

import org.richardstallman.dvback.global.security.jwt.entity.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, String> {

}
