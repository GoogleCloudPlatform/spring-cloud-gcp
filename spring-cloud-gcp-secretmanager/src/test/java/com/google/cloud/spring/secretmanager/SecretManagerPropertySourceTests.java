/*
 * Copyright 2017-2020 the original author or authors.
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

package com.google.cloud.spring.secretmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.protobuf.ByteString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class SecretManagerPropertySourceTests {
  private static final GcpProjectIdProvider DEFAULT_PROJECT_ID_PROVIDER = () -> "defaultProject";

  private SecretManagerTemplate secretManagerTemplate;

  private SecretManagerPropertySource source;

  @BeforeEach
  void setupMocks() {
    this.secretManagerTemplate = mock(SecretManagerTemplate.class);
    source = new SecretManagerPropertySource("name", secretManagerTemplate, DEFAULT_PROJECT_ID_PROVIDER);
  }

  @Test
  void testSecret() {
    String expected = "secret";
    when(this.secretManagerTemplate.getSecretByteString(Mockito.any(SecretVersionName.class))).thenReturn(ByteString.copyFromUtf8(expected));
    assertEquals(expected, source.getProperty("sm://property-name"));
  }

  @Test
  void testMissingSecret() {
    when(this.secretManagerTemplate.getSecretByteString(Mockito.any(SecretVersionName.class))).thenReturn(null);
    assertNull(source.getProperty("sm://property-name"));
  }
}
