package org.richardstallman.dvback.domain.file.domain.request;

import org.richardstallman.dvback.common.constant.CommonConstants.FileType;

public record PostImageRequestDto(
    FileType type, String filePath
) {

}
