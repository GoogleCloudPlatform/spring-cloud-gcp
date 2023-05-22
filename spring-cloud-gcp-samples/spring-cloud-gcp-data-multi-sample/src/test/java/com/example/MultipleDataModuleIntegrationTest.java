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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/** Tests that our Spring Data modules can be used with each other. */
// Please use "-Dit.multisample=true" to enable the tests
@ExtendWith(SpringExtension.class)
@EnabledIfSystemProperty(named = "it.multisample", matches = "true")
@TestPropertySource("classpath:application-test.properties")
@SpringBootTest
class MultipleDataModuleIntegrationTest {

  // The Spanner Repo
  @Autowired TraderRepository traderRepository;

  // The Datastore Repo
  @Autowired PersonRepository datastorePersonRepository;

  @Autowired TraderService traderService;

  @Autowired PersonService personService;

  @Test
  void testMultipleModulesTogether() {

    this.traderRepository.deleteAll();
    this.datastorePersonRepository.deleteAll();

    assertThat(this.traderRepository.count()).isZero();
    assertThat(this.datastorePersonRepository.count()).isZero();

    this.traderRepository.save(new Trader("id1", "trader", "one"));
    this.datastorePersonRepository.save(new Person(1L, "person1"));

    assertThat(this.traderRepository.count()).isEqualTo(1L);
    assertThat(this.datastorePersonRepository.count()).isEqualTo(1L);
  }

  @Test
  void testMultipleModulesTogetherWithTransaction() {

    this.traderService.deleteAll();
    this.personService.deleteAll();

    assertThat(this.traderService.count()).isZero();
    assertThat(this.personService.count()).isZero();

    this.traderService.save(new Trader("id1", "trader", "one"));
    this.personService.save(new Person(1L, "person1"));

    assertThat(this.traderService.count()).isEqualTo(1L);
    assertThat(this.personService.count()).isEqualTo(1L);
  }
}
