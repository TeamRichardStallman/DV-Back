package org.richardstallman.dvback.domain.post.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.common.DvApiResponse;
import org.richardstallman.dvback.domain.post.domain.request.PostCreateRequestDto;
import org.richardstallman.dvback.domain.post.domain.response.PostCreateResponseDto;
import org.richardstallman.dvback.domain.post.domain.response.PostSliceListResponse;
import org.richardstallman.dvback.domain.post.domain.response.PostUserListResponseDto;
import org.richardstallman.dvback.domain.post.service.PostService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/post", produces = MediaType.APPLICATION_JSON_VALUE)
public class PostController {

  private final PostService postService;

  @PostMapping
  public ResponseEntity<DvApiResponse<PostCreateResponseDto>> createPost(
      @Valid @RequestBody final PostCreateRequestDto postCreateRequestDto,
      @AuthenticationPrincipal final Long userId) {
    final PostCreateResponseDto postCreateResponseDto =
        postService.createPost(postCreateRequestDto, userId);
    return ResponseEntity.status(HttpStatus.CREATED).body(DvApiResponse.of(postCreateResponseDto));
  }

  @GetMapping("/user")
  public ResponseEntity<DvApiResponse<PostUserListResponseDto>> getPostsByUser(
      @AuthenticationPrincipal final Long userId) {
    final List<PostCreateResponseDto> posts = postService.getPostsByUserId(userId);
    return ResponseEntity.ok(DvApiResponse.of(new PostUserListResponseDto(posts)));
  }

  @GetMapping("/subscription")
  public ResponseEntity<DvApiResponse<PostSliceListResponse>> getSubscribedPostsByPage(
      @AuthenticationPrincipal final Long userId, Pageable pageable) {
    final Slice<PostCreateResponseDto> posts = postService.getPostBySubscription(userId, pageable);
    return ResponseEntity.ok(
        DvApiResponse.of(
            new PostSliceListResponse(posts.getContent(), posts.getNumber(), posts.isLast())));
  }

  @GetMapping("/search")
  public ResponseEntity<DvApiResponse<PostSliceListResponse>> searchPostByJob(
      @AuthenticationPrincipal final Long userId, @RequestParam String keyword, Pageable pageable) {
    Slice<PostCreateResponseDto> posts = postService.searchPostByContent(userId, keyword, pageable);
    return ResponseEntity.ok(
        DvApiResponse.of(
            new PostSliceListResponse(posts.getContent(), posts.getNumber(), posts.isLast())));
  }
}
