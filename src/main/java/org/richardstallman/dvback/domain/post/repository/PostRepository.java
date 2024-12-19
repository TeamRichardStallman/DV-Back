package org.richardstallman.dvback.domain.post.repository;

import java.util.List;
import java.util.Optional;
import org.richardstallman.dvback.domain.post.domain.PostDomain;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface PostRepository {

  PostDomain save(PostDomain postDomain);

  List<PostDomain> findByAuthorId(Long authorId);

  Optional<PostDomain> findByPostId(Long postId);

  Slice<PostDomain> findByJobIdsPageable(List<Long> jobIds, Pageable pageable);

  Slice<PostDomain> searchByContentPageable(String keyword, Pageable pageable);
}
