package org.richardstallman.dvback.domain.post.repository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.post.converter.PostConverter;
import org.richardstallman.dvback.domain.post.domain.PostDomain;
import org.richardstallman.dvback.domain.post.entity.PostEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

  private final PostConverter postConverter;
  private final PostJpaRepository postJpaRepository;

  @Override
  public PostDomain save(PostDomain postDomain) {
    return postConverter.fromEntityToDomain(
        postJpaRepository.save(postConverter.fromDomainToEntity(postDomain)));
  }

  @Override
  public List<PostDomain> findByAuthorId(Long authorId) {
    return postJpaRepository.findByAuthorUserIdOrderByPostIdDesc(authorId).stream()
        .map(postConverter::fromEntityToDomain)
        .toList();
  }

  @Override
  public Optional<PostDomain> findByPostId(Long postId) {
    return postJpaRepository.findById(postId).map(postConverter::fromEntityToDomain);
  }

  @Override
  public Slice<PostDomain> findByJobIdsPageable(List<Long> jobIds, Pageable pageable) {
    Slice<PostEntity> postEntities = postJpaRepository.findSliceByJobJobIdIn(jobIds, pageable);
    return postEntities.map(postConverter::fromEntityToDomain);
  }

  @Override
  public Slice<PostDomain> searchByContentPageable(String keyword, Pageable pageable) {
    Slice<PostEntity> postEntities =
        postJpaRepository.findSliceByContentContainingIgnoreCase(keyword, pageable);
    return postEntities.map(postConverter::fromEntityToDomain);
  }
}
