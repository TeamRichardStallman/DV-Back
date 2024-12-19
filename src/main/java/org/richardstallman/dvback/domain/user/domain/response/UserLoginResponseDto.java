package org.richardstallman.dvback.domain.user.domain.response;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.richardstallman.dvback.common.constant.CommonConstants;

public record UserLoginResponseDto(
    @NotNull String type,
    Long userId,
    String socialId,
    String email,
    String username,
    String name,
    String nickname,
    String s3ProfileImageUrl,
    Boolean leave,
    CommonConstants.Gender gender,
    LocalDate birthdate) {}
