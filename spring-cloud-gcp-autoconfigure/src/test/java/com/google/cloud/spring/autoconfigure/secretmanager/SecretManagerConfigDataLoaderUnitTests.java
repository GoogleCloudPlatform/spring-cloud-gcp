package com.google.cloud.spring.autoconfigure.secretmanager;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.secretmanager.SecretManagerTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.context.config.ConfigDataLoaderContext;
import org.springframework.boot.context.config.ConfigDataLocation;

/**
 * Unit tests for {@link SecretManagerConfigDataLoader}.
 */

class SecretManagerConfigDataLoaderUnitTests {

  private final ConfigDataLoaderContext loaderContext = mock(ConfigDataLoaderContext.class);
  private final GcpProjectIdProvider idProvider = mock(GcpProjectIdProvider.class);
  private final SecretManagerTemplate template = mock(SecretManagerTemplate.class);
  private final ConfigurableBootstrapContext bootstrapContext = mock(
      ConfigurableBootstrapContext.class);
  private final SecretManagerConfigDataLoader loader = new SecretManagerConfigDataLoader();

  @Test
  void loadIncorrectResourceThrowsException() {
    when(loaderContext.getBootstrapContext()).thenReturn(bootstrapContext);
    when(bootstrapContext.get(GcpProjectIdProvider.class)).thenReturn(idProvider);
    when(bootstrapContext.get(SecretManagerTemplate.class)).thenReturn(template);
    when(template.secretExists(anyString(), anyString())).thenReturn(false);
    SecretManagerConfigDataResource resource = new SecretManagerConfigDataResource(
        ConfigDataLocation.of("fake"));
    assertThatCode(() -> loader.load(loaderContext, resource)).doesNotThrowAnyException();
  }
}
