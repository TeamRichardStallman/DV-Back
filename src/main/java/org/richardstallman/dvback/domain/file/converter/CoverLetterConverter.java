package org.richardstallman.dvback.domain.file.converter;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.file.domain.CoverLetterDomain;
import org.richardstallman.dvback.domain.file.domain.request.CoverLetterRequestDto;
import org.richardstallman.dvback.domain.file.domain.request.FileRequestDto;
import org.richardstallman.dvback.domain.file.domain.response.CoverLetterResponseDto;
import org.richardstallman.dvback.domain.file.entity.CoverLetterEntity;
import org.richardstallman.dvback.domain.user.converter.UserConverter;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.richardstallman.dvback.domain.user.domain.response.UserResponseDto;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoverLetterConverter {

  private final UserConverter userConverter;

  public CoverLetterEntity fromDomainToEntity(CoverLetterDomain coverLetterDomain) {
    return new CoverLetterEntity(
        coverLetterDomain.getCoverLetterId(),
        userConverter.fromDomainToEntity(coverLetterDomain.getUserDomain()),
        coverLetterDomain.getFileName(),
        coverLetterDomain.getS3FileUrl());
  }

  public CoverLetterDomain fromEntityToDomain(CoverLetterEntity coverLetterEntity) {
    return CoverLetterDomain.builder()
        .coverLetterId(coverLetterEntity.getCoverLetterId())
        .userDomain(userConverter.fromEntityToDomain(coverLetterEntity.getUser()))
        .fileName(coverLetterEntity.getFileName())
        .s3FileUrl(coverLetterEntity.getS3FileUrl())
        .build();
  }

  public CoverLetterDomain fromUrlToDomain(
      String coverLetterUrl, UserResponseDto userResponseDto, String fileName) {
    return CoverLetterDomain.builder()
        .userDomain(userConverter.fromResponseDtoToDomain(userResponseDto))
        .fileName(fileName)
        .s3FileUrl(coverLetterUrl)
        .build();
  }

  public CoverLetterResponseDto fromDomainToResponseDto(CoverLetterDomain coverLetterDomain) {
    return new CoverLetterResponseDto(
        coverLetterDomain.getCoverLetterId(),
        coverLetterDomain.getFileName(),
        coverLetterDomain.getS3FileUrl());
  }

  public CoverLetterRequestDto fromFileRequestDtoToRequestDto(FileRequestDto fileRequestDto) {
    return new CoverLetterRequestDto(fileRequestDto.getType(), fileRequestDto.getFilePath());
  }

  public CoverLetterDomain fromRequestDtoToDomain(
      CoverLetterRequestDto coverLetterRequestDto, UserDomain userDomain, String fileName) {
    return CoverLetterDomain.builder()
        .userDomain(userDomain)
        .fileName(fileName)
        .s3FileUrl(coverLetterRequestDto.getFilePath())
        .build();
  }
}
