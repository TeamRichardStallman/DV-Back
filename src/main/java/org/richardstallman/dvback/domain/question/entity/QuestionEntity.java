package org.richardstallman.dvback.domain.question.entity;

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
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.richardstallman.dvback.domain.interview.entity.InterviewEntity;
import org.richardstallman.dvback.global.entity.BaseEntity;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "questions")
public class QuestionEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "question_seq")
  @SequenceGenerator(name = "question_seq", sequenceName = "question_id_seq", allocationSize = 1)
  private Long questionId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "interview_id", nullable = false)
  private InterviewEntity interview;

  @NotNull(message = "Question Text is required") @Column(columnDefinition = "TEXT")
  private String questionText;

  private String s3AudioUrl;
  private String s3VideoUrl;

  @Column(columnDefinition = "TEXT")
  private String questionExcerpt;

  @NotNull(message = "Question Intent is required") @Column(columnDefinition = "TEXT")
  private String questionIntent;

  @NotNull(message = "keyTerms are required") @Column(columnDefinition = "jsonb")
  @JdbcTypeCode(SqlTypes.JSON)
  private List<String> keyTerms;

  private String questionType;
}
