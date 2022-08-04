package com.example;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@ConfigurationProperties("example")
@RefreshScope
public class SecretConfiguration {

  private String property;

  public void setProperty(String property) {
    this.property = property;
  }

  public String getProperty() {
    return property;
  }
}
