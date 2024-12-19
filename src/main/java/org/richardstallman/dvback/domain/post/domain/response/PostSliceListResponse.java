package org.richardstallman.dvback.domain.post.domain.response;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record PostSliceListResponse(
    @NotNull List<PostCreateResponseDto> posts,
    @NotNull Integer currentPage,
    @NotNull Boolean isLastPage) {}
