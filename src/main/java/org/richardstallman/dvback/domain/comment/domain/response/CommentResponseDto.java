package org.richardstallman.dvback.domain.comment.domain.response;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CommentResponseDto(
    @NotNull Long commentId,
    @NotNull Long postId,
    @NotNull String comment,
    @NotNull LocalDateTime generatedAt) {}
