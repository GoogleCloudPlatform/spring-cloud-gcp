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
import com.google.common.hash.Hashing;
import com.google.protobuf.ByteString;
import com.google.protobuf.Int64Value;

/**
 * Offers convenience methods for performing common operations on KMS including
 * encrypting and decrypting text.
 *
 * @author Emmanouil Gkatziouras
 */
public class KmsTemplate implements KmsOperations {

	private final KeyManagementServiceClient client;

	private final GcpProjectIdProvider projectIdProvider;

	public KmsTemplate(
			KeyManagementServiceClient keyManagementServiceClient,
			GcpProjectIdProvider projectIdProvider) {
		this.client = keyManagementServiceClient;
		this.projectIdProvider = projectIdProvider;
	}

	public String encrypt(String cryptoKey, String plaintext) {
		CryptoKeyName cryptoKeyName = KmsPropertyUtils.getCryptoKeyName(cryptoKey, projectIdProvider);

		ByteString plaintextByteString = ByteString.copyFromUtf8(plaintext);

		long crc32c = longCrc32c(plaintextByteString);

		EncryptRequest request = EncryptRequest.newBuilder()
				.setName(cryptoKeyName.toString())
				.setPlaintext(plaintextByteString)
				.setPlaintextCrc32C(
						Int64Value.newBuilder().setValue(crc32c).build())
				.build();

		EncryptResponse response = client.encrypt(request);
		assertCrcMatch(response);

		return encodeBase64(response);
	}

	public String decrypt(String cryptoKey, String encryptedText) {
		CryptoKeyName cryptoKeyName = KmsPropertyUtils.getCryptoKeyName(cryptoKey, projectIdProvider);

		byte[] decodedBytes = decodeBase64(encryptedText);
		ByteString encryptedByteString = ByteString.copyFrom(decodedBytes);
		long crc32c = longCrc32c(encryptedByteString);

		DecryptRequest request =
				DecryptRequest.newBuilder()
						.setName(cryptoKeyName.toString())
						.setCiphertext(encryptedByteString)
						.setCiphertextCrc32C(
								Int64Value.newBuilder().setValue(crc32c).build())
						.build();

		DecryptResponse response = client.decrypt(request);
		assertCrcMatch(response);
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

	private long longCrc32c(ByteString plaintextByteString) {
		return Hashing.crc32c().hashBytes(plaintextByteString.toByteArray()).padToLong();
	}

	private void assertCrcMatch(EncryptResponse response) {
		long expected = response.getCiphertextCrc32C().getValue();
		long received = longCrc32c(response.getCiphertext());

		if (expected != received) {
			throw new KmsException("Encryption: response from server corrupted");
		}
	}

	private void assertCrcMatch(DecryptResponse response) {
		long expected = response.getPlaintextCrc32C().getValue();
		long received = longCrc32c(response.getPlaintext());

		if (expected != received) {
			throw new KmsException("Decryption : response from server corrupted");
		}
	}

}
