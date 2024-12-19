package org.richardstallman.dvback.domain.user.repository;

import java.util.Optional;
import org.richardstallman.dvback.domain.user.domain.UserDomain;

public interface UserRepository {

  Optional<UserDomain> findById(long id);

  Optional<UserDomain> findBySocialId(String socialId);

  UserDomain save(UserDomain userDomain);

  boolean existsByUsername(String username);

  Optional<String> findProfileImageUrlById(Long userId);
}
