package com.google.cloud.spring.autoconfigure.secretmanager;

import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import org.springframework.boot.context.config.ConfigDataLocation;
import org.springframework.boot.context.config.ConfigDataResource;

public class SecretManagerConfigDataResource extends ConfigDataResource {

  private final SecretManagerServiceClient secretManagerServiceClient;
  private final ConfigDataLocation location;

  public SecretManagerConfigDataResource(SecretManagerServiceClient secretManagerServiceClient, ConfigDataLocation location) {
    this.secretManagerServiceClient = secretManagerServiceClient;
    this.location = location;
  }

  public SecretManagerServiceClient getSecretManagerServiceClient() {
    return secretManagerServiceClient;
  }

  public ConfigDataLocation getLocation() {
    return location;
  }
}

