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

package com.google.cloud.spring.kms;

import java.util.Base64;

import com.google.cloud.kms.v1.CryptoKeyName;
import com.google.cloud.kms.v1.DecryptRequest;
import com.google.cloud.kms.v1.DecryptResponse;
import com.google.cloud.kms.v1.EncryptRequest;
import com.google.cloud.kms.v1.EncryptResponse;
import com.google.cloud.kms.v1.KeyManagementServiceClient;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.protobuf.ByteString;

/**
 * Offers convenience methods for performing common operations on KMS including
 * encrypting and decrypting text.
 *
 * @author Emmanouil Gkatziouras
 */
public class KMSTemplate implements KMSOperations {

	private final KeyManagementServiceClient client;

	private final GcpProjectIdProvider projectIdProvider;

	public KMSTemplate(
			KeyManagementServiceClient keyManagementServiceClient,
			GcpProjectIdProvider projectIdProvider) {
		this.client = keyManagementServiceClient;
		this.projectIdProvider = projectIdProvider;
	}

	public String encrypt(String cryptoKey, String plaintext) {
		CryptoKeyName cryptoKeyName = KMSPropertyUtils.getCryptoKeyName(cryptoKey, projectIdProvider);

		ByteString plaintextByteString = ByteString.copyFromUtf8(plaintext);

		EncryptRequest request = EncryptRequest.newBuilder()
				.setName(cryptoKeyName.toString())
				.setPlaintext(plaintextByteString)
				.build();

		EncryptResponse response = client.encrypt(request);

		return encodeBase64(response);
	}

	public String decrypt(String cryptoKey, String encryptedText) {
		CryptoKeyName cryptoKeyName = KMSPropertyUtils.getCryptoKeyName(cryptoKey, projectIdProvider);

		byte[] decodedBytes = decodeBase64(encryptedText);
		ByteString encryptedByteString = ByteString.copyFrom(decodedBytes);

		DecryptRequest request =
				DecryptRequest.newBuilder()
						.setName(cryptoKeyName.toString())
						.setCiphertext(encryptedByteString)
						.build();

		DecryptResponse response = client.decrypt(request);
		return response.getPlaintext().toStringUtf8();
	}

	private String encodeBase64(EncryptResponse response) {
		byte[] bytes = response.getCiphertext().toByteArray();
		byte[] encoded = Base64.getEncoder().encode(bytes);
		return new String(encoded);
	}

	private byte[] decodeBase64(String encryptedText) {
		byte[] bytes = encryptedText.getBytes();
		return Base64.getDecoder().decode(bytes);
	}

}
