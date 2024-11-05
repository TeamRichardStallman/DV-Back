package org.richardstallman.dvback.domain.file.repository.coverletter;

import org.richardstallman.dvback.domain.file.entity.CoverLetterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoverLetterJpaRepository extends JpaRepository<CoverLetterEntity, Long> {}