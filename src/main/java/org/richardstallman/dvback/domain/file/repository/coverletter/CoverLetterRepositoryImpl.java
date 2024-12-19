package org.richardstallman.dvback.domain.file.repository.coverletter;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.file.converter.CoverLetterConverter;
import org.richardstallman.dvback.domain.file.domain.CoverLetterDomain;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CoverLetterRepositoryImpl implements CoverLetterRepository {

  private final CoverLetterConverter coverLetterConverter;
  private final CoverLetterJpaRepository coverLetterJpaRepository;

  @Override
  public CoverLetterDomain save(CoverLetterDomain coverLetterDomain) {
    return coverLetterConverter.fromEntityToDomain(
        coverLetterJpaRepository.save(coverLetterConverter.fromDomainToEntity(coverLetterDomain)));
  }

  @Override
  public CoverLetterDomain findByCoverLetterId(Long coverLetterId) {
    return coverLetterConverter.fromEntityToDomain(
        coverLetterJpaRepository.findById(coverLetterId).orElse(null));
  }

  @Override
  public List<CoverLetterDomain> findCoverLetterListByUserId(Long userId) {
    return coverLetterJpaRepository.findByUserUserId(userId).stream()
        .map(coverLetterConverter::fromEntityToDomain)
        .toList();
  }
}
