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

package com.example;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.google.cloud.spring.autoconfigure.firestore.GcpFirestoreProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@EnabledIfSystemProperty(named = "it.firestore.native", matches = "true")
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-test.properties")
@SpringBootTest(properties = "run.commandline.runner=off")
public class FirestoreSampleAppNativeIntegrationTests {

  private final Logger logger =
      LoggerFactory.getLogger(FirestoreSampleAppNativeIntegrationTests.class);
  @Autowired private ApplicationContext applicationContext;

  @Test
  void credentialsPropertiesAreAccessibleTest() {
    logger.info(
        "This test is only needed for graalvm compilation test"
            + " to verify the encoded-key property is accessible."
            + " Turn on by it.firestore.native=true");
    String encodedKeyFromAutoConfig =
        applicationContext.getBean(GcpFirestoreProperties.class).getCredentials().getEncodedKey();
    assertNotNull(encodedKeyFromAutoConfig);
  }
}
