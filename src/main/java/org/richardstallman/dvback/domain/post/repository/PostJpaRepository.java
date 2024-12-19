package org.richardstallman.dvback.domain.post.repository;

import java.util.List;
import org.richardstallman.dvback.domain.post.entity.PostEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostJpaRepository extends JpaRepository<PostEntity, Long> {

  List<PostEntity> findByAuthorUserIdOrderByPostIdDesc(Long authorId);

  Slice<PostEntity> findSliceByJobJobIdIn(List<Long> jobIds, Pageable pageable);

  Slice<PostEntity> findSliceByContentContainingIgnoreCase(String content, Pageable pageable);
}
