package org.richardstallman.dvback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DvBackApplication {

  public static void main(String[] args) {
    SpringApplication.run(DvBackApplication.class, args);
  }
}
