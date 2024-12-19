package org.richardstallman.dvback.domain.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.common.DvApiResponse;
import org.richardstallman.dvback.domain.comment.domain.request.CommentCreateRequestDto;
import org.richardstallman.dvback.domain.comment.domain.response.CommentDeleteResponseDto;
import org.richardstallman.dvback.domain.comment.domain.response.CommentListResponseDto;
import org.richardstallman.dvback.domain.comment.domain.response.CommentResponseDto;
import org.richardstallman.dvback.domain.comment.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/comment", produces = MediaType.APPLICATION_JSON_VALUE)
public class CommentController {

  private final CommentService commentService;

  @PostMapping
  public ResponseEntity<DvApiResponse<CommentResponseDto>> createComment(
      @Valid @RequestBody final CommentCreateRequestDto commentCreateRequestDto,
      @AuthenticationPrincipal final Long userId) {
    final CommentResponseDto commentResponseDto =
        commentService.createComment(commentCreateRequestDto, userId);
    return ResponseEntity.status(HttpStatus.CREATED).body(DvApiResponse.of(commentResponseDto));
  }

  @DeleteMapping("/{commentId}")
  public ResponseEntity<DvApiResponse<CommentDeleteResponseDto>> deleteComment(
      @PathVariable final Long commentId) {
    final CommentDeleteResponseDto commentDeleteResponseDto =
        commentService.deleteComment(commentId);
    return ResponseEntity.ok(DvApiResponse.of(commentDeleteResponseDto));
  }

  @GetMapping("/post/{postId}")
  public ResponseEntity<DvApiResponse<CommentListResponseDto>> getCommentsByPostId(
      @PathVariable final Long postId) {
    final CommentListResponseDto commentListResponseDto =
        commentService.getCommentListByPostId(postId);
    return ResponseEntity.ok(DvApiResponse.of(commentListResponseDto));
  }

  @GetMapping("/author/{commentAuthorId}")
  public ResponseEntity<DvApiResponse<CommentListResponseDto>> getCommentsByAuthorId(
      @PathVariable final Long commentAuthorId) {
    final CommentListResponseDto commentListResponseDto =
        commentService.getCommentListByCommentAuthorId(commentAuthorId);
    return ResponseEntity.ok(DvApiResponse.of(commentListResponseDto));
  }
}
