package org.richardstallman.dvback.domain.post.domain.response;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.richardstallman.dvback.common.constant.CommonConstants.PostType;
import org.richardstallman.dvback.domain.evaluation.domain.overall.response.OverallEvaluationResponseDto;
import org.richardstallman.dvback.domain.interview.domain.response.InterviewResponseDto;

public record PostCreateResponseDto(
    @NotNull long postId,
    @NotNull long authorId,
    @NotNull String authorNickname,
    @NotNull String authorProfileUrl,
    @NotNull String jobName,
    @NotNull String jobNameKorean,
    @NotNull String content,
    String s3ImageUrl,
    InterviewResponseDto interview,
    OverallEvaluationResponseDto evaluation,
    @NotNull PostType postType,
    @NotNull LocalDateTime generatedAt) {}
