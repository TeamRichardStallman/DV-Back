package org.richardstallman.dvback.global.config.coupons;

import java.util.List;
import lombok.Data;
import org.richardstallman.dvback.global.config.yml.YamlPropertySourceFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource(
    value = "classpath:/properties/${spring.profiles.active}/welcome_coupon.yml",
    factory = YamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "coupon")
public class CouponsConfig {

  private List<WelcomeCoupons> welcomeCoupons;

  @Data
  public static class WelcomeCoupons {

    private int chargeAmount;
    private String couponName;
    private String interviewMode;
    private String interviewAssetType;
  }
}
