/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spring.autoconfigure.parametermanager;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.parametermanager.ParameterManagerTemplate;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.boot.bootstrap.ConfigurableBootstrapContext;
import org.springframework.boot.context.config.ConfigDataLoaderContext;
import org.springframework.boot.context.config.ConfigDataLocation;

/** Unit tests for {@link ParameterManagerConfigDataLoader}. */
class ParameterManagerConfigDataLoaderUnitTests {
  private final ConfigDataLoaderContext loaderContext = mock(ConfigDataLoaderContext.class);
  private final GcpProjectIdProvider idProvider = mock(GcpProjectIdProvider.class);
  private final ParameterManagerTemplate template = mock(ParameterManagerTemplate.class);
  private final GcpParameterManagerProperties properties = mock(GcpParameterManagerProperties.class);
  private final CredentialsProvider credentialsProvider = mock(CredentialsProvider.class);
  private final ConfigurableBootstrapContext bootstrapContext = mock(
      ConfigurableBootstrapContext.class);
  private final ParameterManagerConfigDataLoader loader = new ParameterManagerConfigDataLoader();

  @ParameterizedTest
  @CsvSource({
      "regional-fake, us-central1",
      "fake, "
  })
  void loadIncorrectResourceThrowsException(String resourceName, String location) {
    when(loaderContext.getBootstrapContext()).thenReturn(bootstrapContext);
    when(bootstrapContext.get(GcpProjectIdProvider.class)).thenReturn(idProvider);
    when(bootstrapContext.get(ParameterManagerTemplate.class)).thenReturn(template);
    when(bootstrapContext.get(GcpParameterManagerProperties.class)).thenReturn(properties);
    when(bootstrapContext.get(CredentialsProvider.class)).thenReturn(credentialsProvider);
    when(template.parameterExists(anyString(), anyString())).thenReturn(false);
    ParameterManagerConfigDataResource resource =
        new ParameterManagerConfigDataResource(ConfigDataLocation.of("fake"));
    assertThatCode(() -> loader.load(loaderContext, resource)).doesNotThrowAnyException();
  }
}
