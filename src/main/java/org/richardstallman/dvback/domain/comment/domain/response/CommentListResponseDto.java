package org.richardstallman.dvback.domain.comment.domain.response;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CommentListResponseDto(@NotNull List<CommentResponseDto> comments) {}
