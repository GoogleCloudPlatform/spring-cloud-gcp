package com.google.cloud.spring.autoconfigure.secretmanager;

import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.secretmanager.SecretManagerPropertySource;
import com.google.cloud.spring.secretmanager.SecretManagerTemplate;
import java.io.IOException;
import java.util.Collections;
import org.springframework.boot.context.config.ConfigData;
import org.springframework.boot.context.config.ConfigDataLoader;
import org.springframework.boot.context.config.ConfigDataLoaderContext;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;

public class SecretManagerConfigDataLoader implements
    ConfigDataLoader<SecretManagerConfigDataResource> {

  @Override
  public ConfigData load(
      ConfigDataLoaderContext context,
      SecretManagerConfigDataResource resource) throws IOException, ConfigDataResourceNotFoundException {
    SecretManagerServiceClient secretManagerServiceClient =
        resource.getSecretManagerServiceClient();

    GcpProjectIdProvider projectIdProvider =
        context.getBootstrapContext().get(GcpProjectIdProvider.class);

    SecretManagerTemplate template = new SecretManagerTemplate(secretManagerServiceClient,
        projectIdProvider);
    if (!template.secretExists(resource.getLocation().toString())) {
      throw new ConfigDataResourceNotFoundException(resource);
    }
    SecretManagerPropertySource propertySource = new SecretManagerPropertySource(
        "spring-cloud-gcp-secret-manager", template, projectIdProvider);

    return new ConfigData(Collections.singleton(propertySource));
  }
}
