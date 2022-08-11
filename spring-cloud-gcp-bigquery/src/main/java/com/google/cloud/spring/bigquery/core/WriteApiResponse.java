/*
 * Copyright 2017-2022 the original author or authors.
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

package com.google.cloud.spring.bigquery.core;

import com.google.cloud.bigquery.storage.v1.StorageError;
import java.util.ArrayList;
import java.util.List;

public class WriteApiResponse {
  private List<StorageError> errors = new ArrayList<>();
  private boolean isSuccessful = false;

  public boolean isSuccessful() {
    return isSuccessful;
  }

  public void setSuccessful(boolean successful) {
    isSuccessful = successful;
  }

  public List<StorageError> getErrors() {
    return errors;
  }

  public void addError(StorageError error) {
    errors.add(error);
  }
}
