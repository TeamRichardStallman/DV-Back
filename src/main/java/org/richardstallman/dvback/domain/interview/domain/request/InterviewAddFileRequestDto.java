package org.richardstallman.dvback.domain.interview.domain.request;

import org.richardstallman.dvback.domain.file.domain.request.FileRequestDto;

public record InterviewAddFileRequestDto(Long interviewId, FileRequestDto coverLetter) {}
