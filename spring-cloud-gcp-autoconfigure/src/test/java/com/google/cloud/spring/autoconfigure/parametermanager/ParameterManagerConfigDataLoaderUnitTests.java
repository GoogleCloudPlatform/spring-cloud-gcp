package com.google.cloud.spring.autoconfigure.parametermanager;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.parametermanager.ParameterManagerTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.context.config.ConfigDataLoaderContext;
import org.springframework.boot.context.config.ConfigDataLocation;

/** Unit tests for {@link ParameterManagerConfigDataLoader}. */
class ParameterManagerConfigDataLoaderUnitTests {
  private final ConfigDataLoaderContext loaderContext = mock(ConfigDataLoaderContext.class);
  private final GcpProjectIdProvider idProvider = mock(GcpProjectIdProvider.class);
  private final ParameterManagerTemplate template = mock(ParameterManagerTemplate.class);
  private final ConfigurableBootstrapContext bootstrapContext =
      mock(ConfigurableBootstrapContext.class);
  private final ParameterManagerConfigDataLoader loader = new ParameterManagerConfigDataLoader();

  @Test
  void loadIncorrectResourceThrowsException() {
    when(loaderContext.getBootstrapContext()).thenReturn(bootstrapContext);
    when(bootstrapContext.get(GcpProjectIdProvider.class)).thenReturn(idProvider);
    when(bootstrapContext.get(ParameterManagerTemplate.class)).thenReturn(template);
    when(template.parameterExists(anyString(), anyString())).thenReturn(false);
    ParameterManagerConfigDataResource resource =
        new ParameterManagerConfigDataResource(ConfigDataLocation.of("fake"));
    assertThatCode(() -> loader.load(loaderContext, resource)).doesNotThrowAnyException();
  }
}
