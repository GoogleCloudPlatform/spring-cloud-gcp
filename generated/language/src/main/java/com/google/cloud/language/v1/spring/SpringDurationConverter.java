package com.google.cloud.language.v1.spring;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.threeten.bp.Duration;

@ConfigurationPropertiesBinding
public class SpringDurationConverter implements Converter<java.time.Duration, Duration> {
  // @Override
  // public Duration convert(String source) {
  //   return Duration.parse(source);
  // }
  @Override
  public Duration convert(java.time.Duration source) {
    return Duration.parse(source.toString());
  }
}

