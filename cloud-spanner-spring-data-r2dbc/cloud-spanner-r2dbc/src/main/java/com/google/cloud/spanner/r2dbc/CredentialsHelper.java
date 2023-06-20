/*
 * Copyright 2020-2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.spanner.r2dbc;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Credential instantiation service that can interact with the filesystem.
 */
class CredentialsHelper {

  GoogleCredentials getOauthCredentials(String oauthToken) {
    return new GoogleCredentials(new AccessToken(oauthToken, null));
  }

  GoogleCredentials getDefaultCredentials() {
    try {
      return GoogleCredentials.getApplicationDefault();
    } catch (IOException e) {
      throw new IllegalArgumentException(String.format("Error loading default credentials", e));
    }
  }


  GoogleCredentials getFileCredentials(String filePath) {
    File credentialsFile = new File(filePath);
    if (!credentialsFile.isFile()) {
      throw new IllegalArgumentException(
          String.format("Error reading credential file %s: File does not exist", filePath));
    }
    try (InputStream credentialsStream = new FileInputStream(credentialsFile)) {
      return GoogleCredentials.fromStream(credentialsStream);
    } catch (IOException e) {
      throw new IllegalArgumentException(
          String.format("Error reading credential file %s", filePath), e);
    }
  }

}
