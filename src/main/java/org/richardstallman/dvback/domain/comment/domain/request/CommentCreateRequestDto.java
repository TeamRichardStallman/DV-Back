package org.richardstallman.dvback.domain.comment.domain.request;

import jakarta.validation.constraints.NotNull;

public record CommentCreateRequestDto(@NotNull Long postId, @NotNull String comment) {}
