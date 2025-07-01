/*
 * Copyright 2017-2023 the original author or authors.
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

package com.google.cloud.spring.pubsub.core.subscriber;

import com.google.cloud.pubsub.v1.Subscriber;

/**
 * A customizer of {@link Subscriber.Builder} objects.
 * Can be implemented as a lambda accepting a {@link Subscriber.Builder} and a `String` subscription.
 */
public interface SubscriberCustomizer {
  void apply(Subscriber.Builder subscriberBuilder, String subscription);
}
