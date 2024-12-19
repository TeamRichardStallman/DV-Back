package org.richardstallman.dvback.domain.user.domain.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.richardstallman.dvback.common.constant.CommonConstants;

public record UserRequestDto(
    String s3ProfileImageObjectKey,
    @NotNull(message = "Name is required") String name,
    @NotNull(message = "Username is required") String username,
    @NotNull(message = "Nickname is required") String nickname,
    @NotNull(message = "Birthdate is required") LocalDate birthdate,
    @NotNull(message = "Gender is required") CommonConstants.Gender gender) {}
