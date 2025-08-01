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

package com.google.cloud.spring.logging;

import com.google.cloud.spring.logging.extractors.CloudTraceIdExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** MVC Adapter that adds the {@link TraceIdLoggingWebMvcInterceptor}. */
@Configuration(proxyBeanMethods = false)
public class LoggingWebMvcConfigurer implements WebMvcConfigurer {

  private final TraceIdLoggingWebMvcInterceptor interceptor;

  /**
   * Constructor that accepts an {@link TraceIdLoggingWebMvcInterceptor}. If the given interceptor
   * is null, then a default {@link CloudTraceIdExtractor} is used.
   *
   * @param interceptor the interceptor to use with this configurer. If not provided a {@link
   *     TraceIdLoggingWebMvcInterceptor} is used with the trace ID extractor described above.
   */
  public LoggingWebMvcConfigurer(
      @Autowired(required = false) TraceIdLoggingWebMvcInterceptor interceptor) {
    if (interceptor != null) {
      this.interceptor = interceptor;
    } else {
      this.interceptor = new TraceIdLoggingWebMvcInterceptor(new CloudTraceIdExtractor());
    }
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(this.interceptor);
  }
}
