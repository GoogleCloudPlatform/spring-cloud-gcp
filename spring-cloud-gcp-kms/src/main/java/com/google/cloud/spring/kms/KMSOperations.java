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

import com.google.cloud.spring.core.GcpProjectIdProvider;

/**
 * Describes supported operations that one can perform on the KMS API.
 *
 * <p>For the methods you need specify the secret from GCP KMS by URI string.
 * The following secret URI syntax is supported:
 *
 * 1. Shortest form - specify location ID, key ring ID, and key ID. Project is derived through {@link GcpProjectIdProvider}
 * kms://{location-id}/{key-ring-id}/{key-id}
 *
 * 2.  Short form - specify project ID, location ID, key ring ID, and key ID
 * kms://{project-id}/{location-id}/{key-ring-id}/{key-id}
 *
 * 3. Long form - specify project ID, location ID, key ring ID, and key ID
 * kms://projects/{project-id}/locations/{location-id}/keyRings/{key-ring-id}/cryptoKeys/{key-id}
 *
 * @author Emmanouil Gkatziouras
 */
public interface KMSOperations {

	/**
	 * Encrypt the text using the specified KMS URI string {@code cryptoKey}.
	 *
	 * <p>
	 * An encryption request will be issued using GCP KMS. The encrypted bytes received the response are Bas64 encoded.
	 *
	 * @param cryptoKey The KMS URI string
	 * @param text The text to encrypt
	 * @return The encrypted bytes in Base64 encoding
	 */
	String encrypt(String cryptoKey, String text);

	/**
	 * Decrypt the text using the specified KMS URI string {@code cryptoKey}.
	 *
	 * <p>
	 * An decryption request will be issued using GCP KMS. The encrypted shall be Bas64 decode to extract the encrypted bytes for the request.
	 *
	 * @param cryptoKey The KMS URI string
	 * @param encryptedText The encrypted bytes in Base64 encoding
	 * @return The decrypted text
	 */
	String decrypt(String cryptoKey, String encryptedText);

}
