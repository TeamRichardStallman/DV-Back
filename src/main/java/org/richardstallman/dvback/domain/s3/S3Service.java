package org.richardstallman.dvback.domain.s3;

import java.util.Map;

public interface S3Service {

  String createPreSignedURL(String fileName, Map<String, String> metadata);

  String getDownloadURL(String fileName);
}