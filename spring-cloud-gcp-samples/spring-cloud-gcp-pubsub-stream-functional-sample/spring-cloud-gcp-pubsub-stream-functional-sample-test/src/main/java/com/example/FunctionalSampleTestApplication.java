/*
 * Copyright 2023 Google LLC
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

package com.example;

import com.example.aot.FunctionalSampleRuntimeHints;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

@SpringBootApplication
@ImportRuntimeHints(FunctionalSampleRuntimeHints.class)
public class FunctionalSampleTestApplication {

  public static void main(String[] args) {
    SpringApplicationBuilder sourceBuilder =
        new SpringApplicationBuilder(FunctionalSourceApplication.class)
            .resourceLoader(
                new PropertyRemovingResourceLoader(
                    "spring-cloud-gcp-pubsub-stream-functional-sample-source"));
    sourceBuilder.run();

    SpringApplicationBuilder sinkBuilder =
        new SpringApplicationBuilder(FunctionalSinkApplication.class)
            .resourceLoader(
                new PropertyRemovingResourceLoader(
                    "spring-cloud-gcp-pubsub-stream-functional-sample-sink"));
    sinkBuilder.run();
  }

  /** Resolves the correct /application.properties file for the specific application. */
  static class PropertyRemovingResourceLoader extends DefaultResourceLoader {
    private String moduleName;

    PropertyRemovingResourceLoader(String moduleName) {
      this.moduleName = moduleName;
    }

    @Override
    public Resource getResource(String location) {
      if (location.contains("classpath:/application.properties")) {
        return new FileSystemResource(
            String.format("../%s/src/main/resources/application.properties", this.moduleName));
      }

      return super.getResource(location);
    }
  }
}
