package org.richardstallman.dvback.domain.job.repository;

import org.richardstallman.dvback.domain.job.entity.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobJpaRepository extends JpaRepository<JobEntity, Long> {

  JobEntity findByJobNameKorean(String jobNameKorean);
}
