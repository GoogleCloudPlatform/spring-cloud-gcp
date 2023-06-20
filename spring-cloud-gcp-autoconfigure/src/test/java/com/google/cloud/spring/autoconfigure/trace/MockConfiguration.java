/*
 * Copyright 2017-2019 the original author or authors.
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

package com.google.cloud.spring.autoconfigure.trace;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.RequestMetadataCallback;
import java.util.Collections;
import org.mockito.stubbing.Answer;
import org.springframework.context.annotation.Bean;

/** Spring config for tests. */
public class MockConfiguration {

  // We'll fake a successful call to GCP for the validation of our "credentials"
  @Bean
  private static CredentialsProvider googleCredentials() {
    return () -> {
      Credentials creds = mock(Credentials.class);
      doAnswer(
              (Answer<Void>)
                  invocationOnMock -> {
                    RequestMetadataCallback callback =
                        (RequestMetadataCallback) invocationOnMock.getArguments()[2];
                    callback.onSuccess(Collections.emptyMap());
                    return null;
                  })
          .when(creds)
          .getRequestMetadata(any(), any(), any());
      return creds;
    };
  }
}
