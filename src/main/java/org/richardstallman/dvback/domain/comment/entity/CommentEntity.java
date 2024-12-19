package org.richardstallman.dvback.domain.comment.entity;

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
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.richardstallman.dvback.domain.post.entity.PostEntity;
import org.richardstallman.dvback.domain.user.entity.UserEntity;
import org.richardstallman.dvback.global.entity.BaseEntity;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comments")
public class CommentEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_seq")
  @SequenceGenerator(name = "comment_seq", sequenceName = "comment_id_seq", allocationSize = 1)
  private Long commentId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity commentAuthor;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "post_id", nullable = false)
  private PostEntity commentPost;

  @Size(max = 1000, message = "Comment Content must be 1000 characters or less")
  @Column(length = 1000)
  private String commentContent;

  private LocalDateTime generatedAt;
}
