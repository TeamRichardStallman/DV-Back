package org.richardstallman.dvback.global.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

@Component
public class MultipartJackson2HttpMessageConverter extends AbstractJackson2HttpMessageConverter {
  public MultipartJackson2HttpMessageConverter(ObjectMapper objectMapper) {
    super(objectMapper, MediaType.APPLICATION_JSON, MediaType.MULTIPART_FORM_DATA);
  }

  @Override
  public boolean canWrite(Class<?> clazz, MediaType mediaType) {
    return false;
  }

  @Override
  protected boolean canWrite(MediaType mediaType) {
    return false;
  }
}
