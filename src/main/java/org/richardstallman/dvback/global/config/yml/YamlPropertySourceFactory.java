package org.richardstallman.dvback.global.config.yml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

@Slf4j
public class YamlPropertySourceFactory implements PropertySourceFactory {

  @Override
  public PropertySource<?> createPropertySource(String name, EncodedResource resource)
      throws IOException {
    YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
    factory.setResources(resource.getResource());
    try {
      Properties properties = factory.getObject();
      assert properties != null;
      return new PropertiesPropertySource(
          Objects.requireNonNull(resource.getResource().getFilename()), properties);
    } catch (Exception e) {
      log.error("Configuration resource ({}) can't be loaded", resource.getResource().getURI());
      throw new FileNotFoundException(
          String.format(
              "Configuration resource (%s) cannot be loaded", resource.getResource().getURI()));
    }
  }
}
