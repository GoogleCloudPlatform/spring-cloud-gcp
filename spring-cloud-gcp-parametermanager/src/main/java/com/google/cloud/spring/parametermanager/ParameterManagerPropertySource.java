/*
 * Copyright 2025 Google LLC
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

package com.google.cloud.spring.parametermanager;

import com.google.cloud.parametermanager.v1.ParameterVersionName;
import com.google.cloud.spring.core.GcpProjectIdProvider;
import org.springframework.core.env.EnumerablePropertySource;

public class ParameterManagerPropertySource
    extends EnumerablePropertySource<ParameterManagerTemplate> {
  private final GcpProjectIdProvider projectIdProvider;

  public ParameterManagerPropertySource(
      String propertySourceName,
      ParameterManagerTemplate parameterManagerTemplate,
      GcpProjectIdProvider projectIdProvider) {
    super(propertySourceName, parameterManagerTemplate);
    this.projectIdProvider = projectIdProvider;
  }

  @Override
  public Object getProperty(String name) {
    ParameterVersionName parameterIdentifier =
        ParameterManagerPropertyUtils.getParameterVersionName(name, this.projectIdProvider);

    if (parameterIdentifier != null) {
      return getSource().getParameterByteString(parameterIdentifier);
    }
    return null;
  }

  /**
   * The {@link ParameterManagerPropertySource} is not enumerable, so this always returns an empty
   * array.
   *
   * @return the empty array.
   */
  @Override
  public String[] getPropertyNames() {
    return new String[0];
  }
}
