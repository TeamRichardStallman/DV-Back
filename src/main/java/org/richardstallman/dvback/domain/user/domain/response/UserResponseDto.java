package org.richardstallman.dvback.domain.user.domain.response;

import jakarta.validation.constraints.NotNull;
import java.util.Date;
import org.richardstallman.dvback.common.constant.CommonConstants;

public record UserResponseDto(
    @NotNull Long userId,
    @NotNull String socialId,
    @NotNull String email,
    @NotNull String name,
    @NotNull String nickname,
    @NotNull String s3ProfileImageUrl,
    @NotNull Boolean leave,
    @NotNull CommonConstants.Gender gender,
    @NotNull Date birthdate) {}