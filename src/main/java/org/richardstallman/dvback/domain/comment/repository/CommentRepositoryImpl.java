package org.richardstallman.dvback.domain.comment.repository;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.comment.converter.CommentConverter;
import org.richardstallman.dvback.domain.comment.domain.CommentDomain;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {

  private final CommentJpaRepository commentJpaRepository;
  private final CommentConverter commentConverter;

  @Override
  public CommentDomain save(CommentDomain commentDomain) {
    return commentConverter.fromEntityToDomain(
        commentJpaRepository.save(commentConverter.fromDomainToEntity(commentDomain)));
  }

  @Override
  public void delete(CommentDomain commentDomain) {
    commentJpaRepository.delete(commentConverter.fromDomainToEntity(commentDomain));
  }

  @Override
  public int deleteByPostId(Long postId) {
    return commentJpaRepository.deleteAllByCommentPostPostId(postId);
  }

  @Override
  public List<CommentDomain> findByPostId(Long postId) {
    return commentJpaRepository.findByCommentPostPostIdOrderByCommentId(postId).stream()
        .map(commentConverter::fromEntityToDomain)
        .toList();
  }

  @Override
  public List<CommentDomain> findByCommentAuthorId(Long commentAuthorId) {
    return commentJpaRepository
        .findByCommentAuthorUserIdOrderByCommentIdDesc(commentAuthorId)
        .stream()
        .map(commentConverter::fromEntityToDomain)
        .toList();
  }

  @Override
  public CommentDomain findByCommentId(Long commentId) {
    return commentJpaRepository
        .findById(commentId)
        .map(commentConverter::fromEntityToDomain)
        .orElse(null);
  }
}
