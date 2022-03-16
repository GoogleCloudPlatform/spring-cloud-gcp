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

package com.google.cloud.spring.kms.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.google.cloud.spring.kms.KmsTemplate;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** Integration tests for {@link KmsTemplate}. */
//Please create two keys on cloud-console, both having key-ring as "integration-test-key-ring" and region "us-east1", first named as "test-key" and second as "other-key"
@EnabledIfSystemProperty(named = "it.kms", matches = "true")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {KmsTestConfiguration.class})
class KmsTemplateIntegrationTests {

  @Autowired KmsTemplate kmsTemplate;

  @Test
  void testEncryptDecryptText() {
    String kmsStr = "us-east1/integration-test-key-ring/test-key";
    byte[] encryptedBytes = kmsTemplate.encryptText(kmsStr, "1234");
    String decryptedText = kmsTemplate.decryptText(kmsStr, encryptedBytes);
    assertThat(decryptedText).isEqualTo("1234");
  }

  @Test
  void testEncryptDecryptBytes() {
    String kmsStr = "us-east1/integration-test-key-ring/test-key";
    String originalText = "1234";
    byte[] bytesToEncrypt = originalText.getBytes(StandardCharsets.UTF_8);
    byte[] encryptedBytes = kmsTemplate.encryptBytes(kmsStr, bytesToEncrypt);
    byte[] decryptedBytes = kmsTemplate.decryptBytes(kmsStr, encryptedBytes);
    String resultText = new String(decryptedBytes, StandardCharsets.UTF_8);
    assertThat(resultText).isEqualTo(originalText);
  }

  @Test
  void testEncryptDecryptMissMatch() {
    String kmsStr = "us-east1/integration-test-key-ring/test-key";
    byte[] encryptedBytes = kmsTemplate.encryptText(kmsStr, "1234");

    String kmsStr2 = "us-east1/integration-test-key-ring/other-key";

    assertThatThrownBy(() -> kmsTemplate.decryptText(kmsStr2, encryptedBytes))
            .isInstanceOf(com.google.api.gax.rpc.InvalidArgumentException.class);
  }
}
