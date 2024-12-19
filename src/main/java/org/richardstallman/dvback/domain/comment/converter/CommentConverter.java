package org.richardstallman.dvback.domain.comment.converter;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.comment.domain.CommentDomain;
import org.richardstallman.dvback.domain.comment.domain.request.CommentCreateRequestDto;
import org.richardstallman.dvback.domain.comment.domain.response.CommentDeleteResponseDto;
import org.richardstallman.dvback.domain.comment.domain.response.CommentResponseDto;
import org.richardstallman.dvback.domain.comment.entity.CommentEntity;
import org.richardstallman.dvback.domain.post.converter.PostConverter;
import org.richardstallman.dvback.domain.post.domain.PostDomain;
import org.richardstallman.dvback.domain.user.converter.UserConverter;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentConverter {

  private final UserConverter userConverter;
  private final PostConverter postConverter;

  public CommentDomain fromEntityToDomain(CommentEntity commentEntity) {
    return CommentDomain.builder()
        .commentId(commentEntity.getCommentId())
        .commentAuthorDomain(userConverter.fromEntityToDomain(commentEntity.getCommentAuthor()))
        .commentPostDomain(postConverter.fromEntityToDomain(commentEntity.getCommentPost()))
        .commentContent(commentEntity.getCommentContent())
        .generatedAt(commentEntity.getGeneratedAt())
        .build();
  }

  public CommentEntity fromDomainToEntity(CommentDomain commentDomain) {
    return new CommentEntity(
        commentDomain.getCommentId(),
        userConverter.fromDomainToEntity(commentDomain.getCommentAuthorDomain()),
        postConverter.fromDomainToEntity(commentDomain.getCommentPostDomain()),
        commentDomain.getCommentContent(),
        commentDomain.getGeneratedAt());
  }

  public CommentDomain fromCreateRequestDtoToDomain(
      CommentCreateRequestDto commentCreateRequestDto,
      UserDomain userDomain,
      PostDomain postDomain,
      LocalDateTime generatedAt) {
    return CommentDomain.builder()
        .commentAuthorDomain(userDomain)
        .commentPostDomain(postDomain)
        .commentContent(commentCreateRequestDto.comment())
        .generatedAt(generatedAt)
        .build();
  }

  public CommentResponseDto fromDomainToResponseDto(CommentDomain commentDomain) {
    return new CommentResponseDto(
        commentDomain.getCommentId(),
        commentDomain.getCommentPostDomain().getPostId(),
        commentDomain.getCommentContent(),
        commentDomain.getGeneratedAt());
  }

  public CommentDeleteResponseDto toDeleteResponseDto(int count, String message) {
    return new CommentDeleteResponseDto(count, message);
  }
}
