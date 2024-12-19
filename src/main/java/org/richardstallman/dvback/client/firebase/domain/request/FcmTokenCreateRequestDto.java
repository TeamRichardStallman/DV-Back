package org.richardstallman.dvback.client.firebase.domain.request;

import jakarta.validation.constraints.NotNull;

public record FcmTokenCreateRequestDto(@NotNull String token) {}
