package org.richardstallman.dvback.domain.post.service;

import static org.richardstallman.dvback.common.constant.CommonConstants.*;

import java.util.List;
import org.richardstallman.dvback.domain.post.domain.PostDomain;
import org.richardstallman.dvback.domain.post.domain.request.PostCreateRequestDto;
import org.richardstallman.dvback.domain.post.domain.response.PostCreateResponseDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface PostService {

  PostCreateResponseDto createPost(PostCreateRequestDto postCreateRequestDto, Long userId);

  List<PostCreateResponseDto> getPostsByUserId(Long userId);

  PostDomain getPost(Long postId);

  Slice<PostCreateResponseDto> getPostBySubscription(Long userId, Pageable pageable);
}
