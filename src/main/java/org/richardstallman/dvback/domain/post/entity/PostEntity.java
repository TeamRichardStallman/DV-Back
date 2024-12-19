package org.richardstallman.dvback.domain.post.entity;

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
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.richardstallman.dvback.common.constant.CommonConstants.PostType;
import org.richardstallman.dvback.domain.evaluation.entity.overall.OverallEvaluationEntity;
import org.richardstallman.dvback.domain.interview.entity.InterviewEntity;
import org.richardstallman.dvback.domain.job.entity.JobEntity;
import org.richardstallman.dvback.domain.user.entity.UserEntity;
import org.richardstallman.dvback.global.entity.BaseEntity;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "posts")
public class PostEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "post_seq")
  @SequenceGenerator(name = "post_seq", sequenceName = "post_id_seq", allocationSize = 1)
  private Long postId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity author;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_id", nullable = false)
  private JobEntity job;

  @Size(max = 1000, message = "Content must be 1000 characters or less")
  @Column(length = 1000)
  private String content;

  private String s3ImageUrl;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "interview_id")
  private InterviewEntity interview;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "overall_evaluation_id")
  private OverallEvaluationEntity overallEvaluation;

  @NotNull(message = "Post Type is required") @Enumerated(EnumType.STRING)
  private PostType postType;

  private LocalDateTime generatedAt;
}
