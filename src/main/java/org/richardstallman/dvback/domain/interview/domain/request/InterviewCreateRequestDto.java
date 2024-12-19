package org.richardstallman.dvback.domain.interview.domain.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewMode;
import org.richardstallman.dvback.common.constant.CommonConstants.InterviewType;
import org.richardstallman.dvback.domain.file.domain.request.FileRequestDto;

public record InterviewCreateRequestDto(
    String interviewTitle,
    @NotNull(message = "Interview Type is required") InterviewType interviewType,
    @NotNull(message = "Interview Method is required") InterviewMethod interviewMethod,
    @NotNull(message = "Interview Mode is required") InterviewMode interviewMode,
    @NotNull(message = "Job Id is required") Long jobId,
    int questionCount,
    List<FileRequestDto> files) {}
