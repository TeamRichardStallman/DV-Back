package org.richardstallman.dvback.domain.point.domain.response;

import jakarta.validation.constraints.NotNull;

public record PointResponseDto(@NotNull int balance) {}
