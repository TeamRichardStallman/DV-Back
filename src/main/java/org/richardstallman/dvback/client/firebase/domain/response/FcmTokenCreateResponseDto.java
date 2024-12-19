package org.richardstallman.dvback.client.firebase.domain.response;

import jakarta.validation.constraints.NotNull;

public record FcmTokenCreateResponseDto(@NotNull String message) {}
