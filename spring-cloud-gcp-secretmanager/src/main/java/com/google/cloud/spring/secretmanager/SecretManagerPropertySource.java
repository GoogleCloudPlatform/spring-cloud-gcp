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

package com.google.cloud.spring.secretmanager;

import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.core.env.EnumerablePropertySource;

/**
 * A property source for Secret Manager which accesses the Secret Manager APIs when {@link
 * #getProperty} is called.
 *
 * @since 1.2.2
 */
public class SecretManagerPropertySource extends EnumerablePropertySource<SecretManagerTemplate> {

  private final GcpProjectIdProvider projectIdProvider;
  private Optional<String> location = Optional.empty();

  public SecretManagerPropertySource(
      String propertySourceName,
      SecretManagerTemplate secretManagerTemplate,
      GcpProjectIdProvider projectIdProvider) {
    super(propertySourceName, secretManagerTemplate);
    this.projectIdProvider = projectIdProvider;
  }

  /**
   * Set the location to be used when creating a SecretVersionName from a property string. This
   * property is used when the property string does not contain enough information to create a
   * SecretVersionName.
   *
   * @param location the location to be used when creating a SecretVersionName from a property
   *     string.
   */
  public void setLocation(Optional<String> location) {
    this.location = location;
  }

  /**
   * Returns the location.
   *
   * @return the location
   */
  public Optional<String> getLocation() {
    return location;
  }

  @Override
  public Object getProperty(String name) {
    AtomicReference<SecretVersionName> secretIdentifier = new AtomicReference<>();
    getLocation().ifPresentOrElse(
        location -> secretIdentifier.set(SecretManagerPropertyUtils.getSecretVersionName(
            name, this.projectIdProvider, location
        )),
        () -> secretIdentifier.set(SecretManagerPropertyUtils.getSecretVersionName(
            name, this.projectIdProvider
        ))
    );

    if (secretIdentifier.get() != null) {
      return getSource().getSecretByteString(secretIdentifier.get());
    } else {
      return null;
    }
  }

  /**
   * The {@link SecretManagerPropertySource} is not enumerable, so this always returns an empty
   * array.
   *
   * @return the empty array.
   */
  @Override
  public String[] getPropertyNames() {
    return new String[0];
  }
}
