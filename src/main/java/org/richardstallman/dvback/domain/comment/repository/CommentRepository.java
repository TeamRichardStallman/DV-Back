package org.richardstallman.dvback.domain.comment.repository;

import java.util.List;
import org.richardstallman.dvback.domain.comment.domain.CommentDomain;

public interface CommentRepository {

  CommentDomain save(CommentDomain commentDomain);

  void delete(CommentDomain commentDomain);

  int deleteByPostId(Long postId);

  List<CommentDomain> findByPostId(Long postId);

  List<CommentDomain> findByCommentAuthorId(Long commentAuthorId);

  CommentDomain findByCommentId(Long commentId);
}
