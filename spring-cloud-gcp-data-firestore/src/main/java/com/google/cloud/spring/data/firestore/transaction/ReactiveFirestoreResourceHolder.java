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

package com.google.cloud.spring.data.firestore.transaction;

import com.google.firestore.v1.Write;
import com.google.protobuf.ByteString;
import java.util.ArrayList;
import java.util.List;
import org.springframework.transaction.support.ResourceHolderSupport;

/**
 * Firestore specific resource holder, {@link ReactiveFirestoreTransactionManager} binds instances
 * of this class to the subscriber context.
 */
public class ReactiveFirestoreResourceHolder extends ResourceHolderSupport {
  private com.google.protobuf.ByteString transactionId;

  private List<Write> writes = new ArrayList<>();

  private List<Object> entities = new ArrayList<>();

  public ReactiveFirestoreResourceHolder(ByteString transactionId) {
    this.transactionId = transactionId;
  }

  public ByteString getTransactionId() {
    return this.transactionId;
  }

  public List<Write> getWrites() {
    return this.writes;
  }

  public List<Object> getEntities() {
    return entities;
  }
}
