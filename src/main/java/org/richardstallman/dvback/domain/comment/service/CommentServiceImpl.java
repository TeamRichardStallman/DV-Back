package org.richardstallman.dvback.domain.comment.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.comment.converter.CommentConverter;
import org.richardstallman.dvback.domain.comment.domain.CommentDomain;
import org.richardstallman.dvback.domain.comment.domain.request.CommentCreateRequestDto;
import org.richardstallman.dvback.domain.comment.domain.response.CommentDeleteResponseDto;
import org.richardstallman.dvback.domain.comment.domain.response.CommentListResponseDto;
import org.richardstallman.dvback.domain.comment.domain.response.CommentResponseDto;
import org.richardstallman.dvback.domain.comment.repository.CommentRepository;
import org.richardstallman.dvback.domain.post.domain.PostDomain;
import org.richardstallman.dvback.domain.post.service.PostService;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.richardstallman.dvback.domain.user.repository.UserRepository;
import org.richardstallman.dvback.global.advice.exceptions.ApiException;
import org.richardstallman.dvback.global.util.TimeUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

  private final CommentConverter commentConverter;
  private final CommentRepository commentRepository;
  private final UserRepository userRepository;
  private final PostService postService;

  @Override
  public CommentResponseDto createComment(
      CommentCreateRequestDto commentCreateRequestDto, Long userId) {
    UserDomain userDomain = getUser(userId);
    PostDomain postDomain = getPost(commentCreateRequestDto.postId());
    LocalDateTime generatedAt = TimeUtil.getCurrentDateTime();
    CommentDomain commentDomain =
        commentRepository.save(
            commentConverter.fromCreateRequestDtoToDomain(
                commentCreateRequestDto, userDomain, postDomain, generatedAt));
    return commentConverter.fromDomainToResponseDto(commentDomain);
  }

  @Override
  public CommentDeleteResponseDto deleteComment(Long commentId) {
    CommentDomain commentDomain = getComment(commentId);
    commentRepository.delete(commentDomain);
    return commentConverter.toDeleteResponseDto(1, "댓글이 삭제되었습니다.");
  }

  @Override
  public int deleteCommentsByPostId(Long postId) {
    return commentRepository.deleteByPostId(postId);
  }

  @Override
  public CommentListResponseDto getCommentListByPostId(Long postId) {
    List<CommentDomain> commentDomains = commentRepository.findByPostId(postId);
    return new CommentListResponseDto(
        commentDomains.stream().map(commentConverter::fromDomainToResponseDto).toList());
  }

  @Override
  public CommentListResponseDto getCommentListByCommentAuthorId(Long commentAuthorId) {
    List<CommentDomain> commentDomains = commentRepository.findByCommentAuthorId(commentAuthorId);
    return new CommentListResponseDto(
        commentDomains.stream().map(commentConverter::fromDomainToResponseDto).toList());
  }

  private CommentDomain getComment(Long commentId) {
    CommentDomain commentDomain = commentRepository.findByCommentId(commentId);
    if (commentDomain == null) {
      throw new ApiException(
          HttpStatus.BAD_REQUEST, String.format("Comment Id (%d) not found.", commentId));
    }
    return commentDomain;
  }

  private UserDomain getUser(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(
            () ->
                new ApiException(
                    HttpStatus.NOT_FOUND, String.format("(%d): User Not Found", userId)));
  }

  private PostDomain getPost(Long postId) {
    return postService.getPost(postId);
  }
}
