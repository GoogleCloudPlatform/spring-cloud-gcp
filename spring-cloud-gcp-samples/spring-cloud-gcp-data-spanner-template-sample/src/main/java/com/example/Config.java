package com.example;

import com.google.cloud.spring.data.spanner.repository.config.EnableSpannerAuditing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import java.util.Optional;

@Configuration
@EnableSpannerAuditing
public class Config {

  @Bean
  public AuditorAware<String> auditorProvider() {
    return () -> Optional.of("YOUR_USERNAME_HERE");
  }
}
