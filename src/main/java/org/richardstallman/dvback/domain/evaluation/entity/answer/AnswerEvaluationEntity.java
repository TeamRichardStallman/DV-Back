package org.richardstallman.dvback.domain.evaluation.entity.answer;

import jakarta.persistence.Column;
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
import org.richardstallman.dvback.domain.evaluation.entity.overall.OverallEvaluationEntity;
import org.richardstallman.dvback.domain.question.entity.QuestionEntity;
import org.richardstallman.dvback.global.entity.BaseEntity;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ansewr_evaluations")
public class AnswerEvaluationEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "answer_evaluation_seq")
  @SequenceGenerator(
      name = "answer_evaluation_seq",
      sequenceName = "answer_evaluation_id_seq",
      allocationSize = 1)
  @Column(name = "answer_evaluation_id")
  private Long answerEvaluationId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "question_id", nullable = false)
  private QuestionEntity question;

  @JoinColumn(name = "answer_feedback_text", nullable = false)
  private String answerFeedbackText;

  @JoinColumn(name = "score", nullable = false)
  private int score;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "overall_evaluation_id")
  private OverallEvaluationEntity overallEvaluation;
}