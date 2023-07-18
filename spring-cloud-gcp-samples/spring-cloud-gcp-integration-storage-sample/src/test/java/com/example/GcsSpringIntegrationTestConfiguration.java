/*
 * Copyright 2023 Google LLC
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

package com.example;

import com.google.cloud.spring.storage.integration.inbound.GcsInboundFileSynchronizer;
import com.google.cloud.spring.storage.integration.inbound.GcsInboundFileSynchronizingMessageSource;
import java.io.File;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.core.MessageSource;

@TestConfiguration
public class GcsSpringIntegrationTestConfiguration {

  private String uniqueDirectory;

  public GcsSpringIntegrationTestConfiguration(
      @Value("${gcs-local-directory}") String localDirectory) {
    uniqueDirectory = String.format("%s-%s", localDirectory, UUID.randomUUID());
  }

  @Bean
  public String uniqueDirectory() {
    return uniqueDirectory;
  }

  @Bean
  @Primary
  @InboundChannelAdapter(channel = "new-file-channel", poller = @Poller(fixedDelay = "5000"))
  public MessageSource<File> synchronizerAdapterOverride(GcsInboundFileSynchronizer synchronizer) {
    GcsInboundFileSynchronizingMessageSource syncAdapter =
        new GcsInboundFileSynchronizingMessageSource(synchronizer);
    syncAdapter.setLocalDirectory(Paths.get(uniqueDirectory).toFile());
    return syncAdapter;
  }
}
