package org.richardstallman.dvback.domain.user.domain.response;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.richardstallman.dvback.common.constant.CommonConstants;

public record UserResponseDto(
    @NotNull Long userId,
    @NotNull String socialId,
    @NotNull String email,
    @NotNull String username,
    @NotNull String name,
    @NotNull String nickname,
    String s3ProfileImageUrl,
    @NotNull Boolean leave,
    @NotNull CommonConstants.Gender gender,
    @NotNull LocalDate birthdate) {}
