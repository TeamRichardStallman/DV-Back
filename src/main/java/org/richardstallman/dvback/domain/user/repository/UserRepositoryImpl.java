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
    return userJpaRepository.findById(id).map(userConverter::fromEntityToDomain);
  }

  @Override
  public Optional<UserDomain> findBySocialId(String socialId) {
    return userJpaRepository.findBySocialId(socialId).map(userConverter::fromEntityToDomain);
  }

  @Override
  public UserDomain save(UserDomain userDomain) {
    return userConverter.fromEntityToDomain(
        userJpaRepository.save(userConverter.fromDomainToEntity(userDomain)));
  }

  @Override
  public boolean existsByUsername(String username) {
    return userJpaRepository.existsByUsername(username);
  }

  @Override
  public Optional<String> findProfileImageUrlById(Long userId) {
    return userJpaRepository.findProfileImageUrlByUserId(userId);
  }
}
