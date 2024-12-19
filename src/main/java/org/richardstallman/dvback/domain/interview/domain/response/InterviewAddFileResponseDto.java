package org.richardstallman.dvback.domain.interview.domain.response;

import jakarta.validation.constraints.NotNull;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewStatus;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewType;
import org.richardstallman.dvback.domain.file.domain.response.FileResponseDto;
import org.richardstallman.dvback.domain.job.domain.JobDomain;

public record InterviewAddFileResponseDto(
    @NotNull Long interviewId,
    @NotNull String interviewTitle,
    @NotNull InterviewStatus interviewStatus,
    @NotNull InterviewType interviewType,
    @NotNull InterviewMethod interviewMethod,
    @NotNull InterviewMode interviewMode,
    @NotNull int questionCount,
    @NotNull JobDomain job,
    FileResponseDto file) {}
