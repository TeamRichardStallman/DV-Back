package org.richardstallman.dvback.domain.comment.repository;

import java.util.List;
import org.richardstallman.dvback.domain.comment.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepository extends JpaRepository<CommentEntity, Long> {

  int deleteAllByCommentPostPostId(Long commentPostId);

  List<CommentEntity> findByCommentPostPostIdOrderByCommentId(Long commentPostId);

  List<CommentEntity> findByCommentAuthorUserIdOrderByCommentIdDesc(Long commentAuthorId);
}
