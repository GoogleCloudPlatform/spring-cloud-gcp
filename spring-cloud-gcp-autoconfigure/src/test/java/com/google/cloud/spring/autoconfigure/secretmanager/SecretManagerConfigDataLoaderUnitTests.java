package com.google.cloud.spring.autoconfigure.secretmanager;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.secretmanager.SecretManagerPropertySource;
import com.google.cloud.spring.secretmanager.SecretManagerTemplate;
import java.util.Optional;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.context.config.ConfigData;
import org.springframework.boot.context.config.ConfigDataLoaderContext;
import org.springframework.boot.context.config.ConfigDataLocation;

/**
 * Unit tests for {@link SecretManagerConfigDataLoader}.
 */

class SecretManagerConfigDataLoaderUnitTests {

  private final ConfigDataLoaderContext loaderContext = mock(ConfigDataLoaderContext.class);
  private final GcpProjectIdProvider idProvider = mock(GcpProjectIdProvider.class);
  private final SecretManagerTemplate template = mock(SecretManagerTemplate.class);
  private final GcpSecretManagerProperties properties = mock(GcpSecretManagerProperties.class);
  private final ConfigurableBootstrapContext bootstrapContext = mock(
      ConfigurableBootstrapContext.class);
  private final SecretManagerConfigDataLoader loader = new SecretManagerConfigDataLoader();

  @ParameterizedTest
  @CsvSource({
      "regional-fake, us-central1",
      "fake, "
  })
  void loadIncorrectResourceThrowsException(String resourceName, String location) {
    when(loaderContext.getBootstrapContext()).thenReturn(bootstrapContext);
    when(bootstrapContext.get(GcpProjectIdProvider.class)).thenReturn(idProvider);
    when(bootstrapContext.get(SecretManagerTemplate.class)).thenReturn(template);
    when(bootstrapContext.get(GcpSecretManagerProperties.class)).thenReturn(properties);
    when(template.secretExists(anyString(), anyString())).thenReturn(false);
    when(properties.getLocation()).thenReturn(Optional.ofNullable(location));
    SecretManagerConfigDataResource resource = new SecretManagerConfigDataResource(
        ConfigDataLocation.of(resourceName));
    assertThatCode(() -> {
      ConfigData configData = loader.load(loaderContext, resource);
      SecretManagerPropertySource propertySource =
          (SecretManagerPropertySource) configData.getPropertySources().get(0);
      assertEquals(Optional.ofNullable(location), propertySource.getLocation());
    }).doesNotThrowAnyException();
  }
}
