package org.richardstallman.dvback.domain.interview.domain.response;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.richardstallman.dvback.common.constant.CommonConstants;
import org.richardstallman.dvback.domain.file.domain.response.FileResponseDto;
import org.richardstallman.dvback.domain.job.domain.JobDomain;

public record InterviewResponseDto(
    @NotNull Long interviewId,
    @NotNull String interviewTitle,
    @NotNull CommonConstants.InterviewStatus interviewStatus,
    @NotNull CommonConstants.InterviewType interviewType,
    @NotNull CommonConstants.InterviewMethod interviewMethod,
    @NotNull CommonConstants.InterviewMode interviewMode,
    @NotNull int questionCount,
    @NotNull JobDomain job,
    List<FileResponseDto> files) {}
