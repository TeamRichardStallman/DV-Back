package org.richardstallman.dvback.domain.user.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.user.converter.UserConverter;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

  private final UserJpaRepository userJpaRepository;
  private final UserConverter userConverter;

  @Override
  public Optional<UserDomain> findById(long id) {
    return userJpaRepository.findById(id).map(userConverter::toDomain);
  }

  @Override
  public Optional<UserDomain> findBySocialId(String socialId) {
    return userJpaRepository.findBySocialId(socialId).map(userConverter::toDomain);
  }

  @Override
  public UserDomain save(UserDomain userDomain) {
    return userConverter.toDomain(userJpaRepository.save(userConverter.toEntity(userDomain)));
  }
}