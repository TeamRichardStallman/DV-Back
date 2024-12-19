package org.richardstallman.dvback.domain.answer.entity;

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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.richardstallman.dvback.domain.question.entity.QuestionEntity;
import org.richardstallman.dvback.global.entity.BaseEntity;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(
    name = "answers",
    uniqueConstraints = {@UniqueConstraint(columnNames = "question_id")})
public class AnswerEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "answer_seq")
  @SequenceGenerator(name = "answer_seq", sequenceName = "answer_id_seq", allocationSize = 1)
  private Long answerId;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "question_id", nullable = false)
  private QuestionEntity question;

  @NotNull(message = "Answer Text is required") @Column(columnDefinition = "TEXT")
  private String answerText;

  private String s3AudioUrl;
  private String s3VideoUrl;

  public AnswerEntity(
      QuestionEntity question, String answerText, String s3AudioUrl, String s3VideoUrl) {
    super();
    this.question = question;
    this.answerText = answerText;
    this.s3AudioUrl = s3AudioUrl;
    this.s3VideoUrl = s3VideoUrl;
  }
}
