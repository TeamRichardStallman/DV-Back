package org.richardstallman.dvback.domain.evaluation.entity.answer;

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
import org.richardstallman.dvback.common.constant.CommonConstants.AnswerEvaluationScore;
import org.richardstallman.dvback.common.constant.CommonConstants.AnswerEvaluationType;
import org.richardstallman.dvback.global.entity.BaseEntity;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "answer_evaluation_scores")
public class AnswerEvaluationScoreEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "answer_evaluation_score_seq")
  @SequenceGenerator(
      name = "answer_evaluation_score_seq",
      sequenceName = "answer_evaluation_score_id_seq",
      allocationSize = 1)
  @Column(name = "answer_evaluation_score_id")
  private Long answerEvaluationScoreId;

  @NotNull(message = "Answer Evaluation Score is required") @Enumerated(EnumType.STRING)
  private AnswerEvaluationScore answerEvaluationScoreName;

  @NotNull(message = "Score is required") private int score;

  @NotNull(message = "Rationale is required") @Column(columnDefinition = "TEXT")
  private String rationale;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "answer_evaluation_id")
  private AnswerEvaluationEntity answerEvaluationEntity;

  @Enumerated(EnumType.STRING)
  private AnswerEvaluationType answerEvaluationType;
}
