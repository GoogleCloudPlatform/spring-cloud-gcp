/*
 * Copyright 2024 Google LLC
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

package com.google.cloud.spring.autoconfigure;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.auth.CredentialTypeForMetrics;
import com.google.auth.Credentials;

public class TestUtils {
  public static final Credentials MOCK_CREDENTIALS = createMockCredentials();

  private static Credentials createMockCredentials() {
    Credentials mockCredential = mock(Credentials.class);
    when(mockCredential.getMetricsCredentialType())
        .thenReturn(CredentialTypeForMetrics.DO_NOT_SEND);
    return mockCredential;
  }

  public static Credentials getMockCredentials() {
    return MOCK_CREDENTIALS;
  }
}
