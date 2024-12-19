package org.richardstallman.dvback.domain.file.domain.response;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CoverLetterListResponseDto(@NotNull List<CoverLetterResponseDto> coverLetters) {}
