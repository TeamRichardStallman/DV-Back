package org.richardstallman.dvback.domain.post.domain.request;

import jakarta.validation.constraints.NotNull;
import org.richardstallman.dvback.common.constant.CommonConstants.PostType;

public record PostCreateRequestDto(
    String jobKoreanName,
    @NotNull String content,
    @NotNull String s3ImageUrl,
    Long interviewId,
    Long overallEvaluationId,
    PostType postType) {}
