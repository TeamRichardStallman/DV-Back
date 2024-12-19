package org.richardstallman.dvback.domain.evaluation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.richardstallman.dvback.common.constant.CommonConstants.EvaluationCriteria;
import org.richardstallman.dvback.domain.evaluation.entity.overall.OverallEvaluationEntity;
import org.richardstallman.dvback.global.entity.BaseEntity;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "evaluation_criteria")
public class EvaluationCriteriaEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "evaluation_criteria_seq")
  @SequenceGenerator(
      name = "evaluation_criteria_seq",
      sequenceName = "evaluation_criteria_id_seq",
      allocationSize = 1)
  @Column(name = "evaluation_criteria_id")
  private Long evaluationCriteriaId;

  @NotNull(message = "Evaluation Criteria is required") @Enumerated(EnumType.STRING)
  private EvaluationCriteria evaluationCriteriaName;

  @NotNull(message = "Feedback Text is required") @Column(columnDefinition = "TEXT")
  private String feedbackText;

  @NotNull(message = "Score is required") private int score;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "overall_evaluation_id")
  private OverallEvaluationEntity overallEvaluation;
}
