package org.richardstallman.dvback.domain.question.domain.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.richardstallman.dvback.common.constant.CommonConstants;
import org.richardstallman.dvback.domain.file.domain.request.FileRequestDto;

public record QuestionRequestListDto(
    @NotNull Long interviewId,
    @NotNull String interviewTitle,
    @NotNull CommonConstants.InterviewStatus interviewStatus,
    @NotNull CommonConstants.InterviewType interviewType,
    @NotNull CommonConstants.InterviewMethod interviewMethod,
    @NotNull CommonConstants.InterviewMode interviewMode,
    int questionCount,
    List<FileRequestDto> files,
    @NotNull Long jobId) {}
