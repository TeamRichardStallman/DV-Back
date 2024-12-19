package org.richardstallman.dvback.domain.interview.repository;

import java.util.List;
import org.richardstallman.dvback.domain.interview.entity.InterviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InterviewJpaRepository extends JpaRepository<InterviewEntity, Long> {

  List<InterviewEntity> findByUserUserIdOrderByInterviewIdDesc(Long userId);

  @Query(
      "SELECT i FROM InterviewEntity i "
          + "JOIN i.user u "
          + "JOIN OverallEvaluationEntity oe ON oe.interview = i "
          + "WHERE u.userId = :userId")
  List<InterviewEntity> findByUserUserIdWithEvaluationsOrderByInterviewIdDesc(
      @Param("userId") Long userId);
}
