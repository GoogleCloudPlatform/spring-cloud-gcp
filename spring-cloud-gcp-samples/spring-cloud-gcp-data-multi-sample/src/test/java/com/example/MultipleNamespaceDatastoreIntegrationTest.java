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

package com.example;

import com.google.cloud.spring.autoconfigure.datastore.DatastoreNamespaceProvider;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** Integration test for multiple-namespace support. */
// Please use "-Dit.datastore=true" to enable the tests
@EnabledIfSystemProperty(named = "it.datastore", matches = "true")
@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application-test.properties")
@EnableAutoConfiguration
class MultipleNamespaceDatastoreIntegrationTest {

  @Autowired PersonRepository datastorePersonRepository;

  @Test
  void testMultipleNamespaces() {

    this.datastorePersonRepository.deleteAll();
    Config.flipNamespace();
    this.datastorePersonRepository.deleteAll();

    Awaitility.await()
        .atMost(15, TimeUnit.SECONDS)
        .until(() -> this.datastorePersonRepository.count() == 0);
    Config.flipNamespace();
    Awaitility.await()
        .atMost(15, TimeUnit.SECONDS)
        .until(() -> this.datastorePersonRepository.count() == 0);

    this.datastorePersonRepository.save(new Person(1L, "a"));
    Config.flipNamespace();
    this.datastorePersonRepository.save(new Person(2L, "a"));
    Config.flipNamespace();
    this.datastorePersonRepository.save(new Person(3L, "a"));

    Awaitility.await()
        .atMost(15, TimeUnit.SECONDS)
        .until(() -> this.datastorePersonRepository.count() == 2);
    Config.flipNamespace();
    Awaitility.await()
        .atMost(15, TimeUnit.SECONDS)
        .until(() -> this.datastorePersonRepository.count() == 1);
  }

  /**
   * Configuring custom multiple namespaces.
   *
   * @author Chengyuan Zhao
   */
  @Configuration
  static class Config {

    static boolean namespaceFlipper;

    /** Flips the namespace that all Datastore repositories and templates use. */
    static void flipNamespace() {
      namespaceFlipper = !namespaceFlipper;
    }

    @Bean
    public DatastoreNamespaceProvider namespaceProvider() {
      return () -> namespaceFlipper ? "n1" : "n2";
    }
  }
}
