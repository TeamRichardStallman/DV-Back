package org.richardstallman.dvback.domain.evaluation.entity.overall;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.richardstallman.dvback.domain.interview.entity.InterviewEntity;
import org.richardstallman.dvback.global.entity.BaseEntity;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "overall_evaluations")
public class OverallEvaluationEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "overall_evaluation_seq")
  @SequenceGenerator(
      name = "overall_evaluation_seq",
      sequenceName = "overall_evaluations_id_seq",
      allocationSize = 1)
  private Long overallEvaluationId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "interview_id", nullable = false)
  private InterviewEntity interview;
}
