package org.richardstallman.dvback.domain.comment.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.richardstallman.dvback.domain.post.domain.PostDomain;
import org.richardstallman.dvback.domain.user.domain.UserDomain;

@Builder
@Getter
public class CommentDomain {

  private Long commentId;
  private UserDomain commentAuthorDomain;
  private PostDomain commentPostDomain;
  private String commentContent;
  private LocalDateTime generatedAt;
}
