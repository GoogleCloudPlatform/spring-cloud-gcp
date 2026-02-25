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

package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * These tests verifies that the pubsub-integration-sample works.
 *
 * @since 1.1
 */
@EnabledIfSystemProperty(named = "it.pubsub-integration", matches = "true")
@ExtendWith({OutputCaptureExtension.class, SpringExtension.class})
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = {"server.port=8082"},
    classes = {SenderReceiverApplication.class, SampleAppIntegrationTest.TestConfig.class})
class SampleAppIntegrationTest {

  @Configuration
  static class TestConfig {
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
      ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
      scheduler.setPoolSize(1);
      scheduler.setThreadNamePrefix("test-task-scheduler-");
      scheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
      return scheduler;
    }
  }

  private RestTemplate restTemplate = new RestTemplate();

  @Test
  void testSample(CapturedOutput capturedOutput) throws Exception {
    MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    String message = "test message " + UUID.randomUUID();
    map.add("message", message);
    map.add("times", 1);

    this.restTemplate.postForObject("http://localhost:8082/postMessage", map, String.class);

    boolean messageReceived = false;
    for (int i = 0; i < 100; i++) {
      if (capturedOutput.toString().contains("Message arrived! Payload: " + message)) {
        messageReceived = true;
        break;
      }
      Thread.sleep(100);
    }
    assertThat(messageReceived).isTrue();
  }
}
