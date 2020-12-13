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

import com.google.cloud.spring.kms.KMSTemplate;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

/**
 * Integration tests for {@link KMSTemplate}.
 *
 * @author Emmanouil Gkatziouras
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { KMSTestConfiguration.class })
public class KMSTemplateIntegrationTests {

	@Autowired
	KMSTemplate kmsTemplate;

	@BeforeClass
	public static void prepare() {
		assumeThat(System.getProperty("it.kms"))
				.as("KMS integration tests are disabled. "
						+ "Please use '-Dit.kms=true' to enable them.")
				.isEqualTo("true");
	}

	@Test
	public void testEncryptDecrypt() {
		String kmsStr = "kms://project-name/europe-west2/spring-cloud-gcp/key-id";
		String encryptedText = kmsTemplate.encrypt(kmsStr, "1234");

		String decryptedText = kmsTemplate.decrypt(kmsStr, encryptedText);

		assertThat(decryptedText).isEqualTo("1234");
	}

	@Test(expected = com.google.api.gax.rpc.InvalidArgumentException.class)
	public void testEncryptDecryptMissMatch() {
		String kmsStr = "kms://project-name/europe-west2/spring-cloud-gcp/key-id";
		String encryptedText = kmsTemplate.encrypt(kmsStr, "1234");

		String kmsStr2 = "kms://project-name/europe-west2/spring-cloud-gcp/key-id-2";
		kmsTemplate.decrypt(kmsStr2, encryptedText);
	}

	/**
	 * On a fine tuned Service Account with only Encrypt/Decrypt permissions towards a KeyRing, trying to use another KMS even on a non existent KeyRing leads to PermissionDeniedException.
	 */
	@Test(expected = com.google.api.gax.rpc.PermissionDeniedException.class)
	public void testUnAuthorisedEncrypt() {
		String kmsStr = "kms://project-name/europe-west2/does-not-exist/key-id";
		kmsTemplate.encrypt(kmsStr, "test-NON-EXISTING-keyring");
	}

	@Test(expected = com.google.api.gax.rpc.NotFoundException.class)
	public void testEncryptMissingKey() {
		String kmsStr = "kms://project-name/europe-west2/spring-cloud-gcp/does-not-exist";
		kmsTemplate.encrypt(kmsStr, "test-NON-EXISTING-key");
	}

}
