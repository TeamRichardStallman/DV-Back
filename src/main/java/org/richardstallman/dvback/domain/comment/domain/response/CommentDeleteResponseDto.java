package org.richardstallman.dvback.domain.comment.domain.response;

import jakarta.validation.constraints.NotNull;

public record CommentDeleteResponseDto(@NotNull int count, @NotNull String message) {}
