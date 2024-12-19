package org.richardstallman.dvback.domain.comment.service;

import org.richardstallman.dvback.domain.comment.domain.request.CommentCreateRequestDto;
import org.richardstallman.dvback.domain.comment.domain.response.CommentDeleteResponseDto;
import org.richardstallman.dvback.domain.comment.domain.response.CommentListResponseDto;
import org.richardstallman.dvback.domain.comment.domain.response.CommentResponseDto;

public interface CommentService {

  CommentResponseDto createComment(CommentCreateRequestDto commentCreateRequestDto, Long userId);

  CommentDeleteResponseDto deleteComment(Long commentId);

  int deleteCommentsByPostId(Long postId);

  CommentListResponseDto getCommentListByPostId(Long postId);

  CommentListResponseDto getCommentListByCommentAuthorId(Long commentAuthorId);
}
