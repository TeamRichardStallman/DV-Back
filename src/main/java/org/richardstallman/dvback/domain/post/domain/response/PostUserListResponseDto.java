package org.richardstallman.dvback.domain.post.domain.response;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PostUserListResponseDto(@NotNull List<PostCreateResponseDto> posts) {}
