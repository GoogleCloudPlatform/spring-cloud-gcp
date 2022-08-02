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

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.spring.data.firestore.FirestoreTemplate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@EnabledIfSystemProperty(named = "it.firestore", matches = "true")
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = WebEnvironment.RANDOM_PORT,
    classes = FirestoreSampleApplication.class)
@TestPropertySource("classpath:application-test.properties")
class FirestoreSampleApplicationIntegrationTests {
  private static final User ALPHA_USER =
      new User("Alpha", 49, singletonList(new Pet("rat", "Snowflake")));
  private static final List<PhoneNumber> ALPHA_PHONE_NUMBERS =
      Arrays.asList(new PhoneNumber("555666777"), new PhoneNumber("777666555"));
  private static final User BETA_USER = new User("Beta", 23, emptyList());
  private static final User DELTA_USER =
      new User("Delta", 49, Arrays.asList(new Pet("fish", "Dory"), new Pet("spider", "Man")));

  private static final User NULL_NAME_USER = new User(null, 23, emptyList());

  @Autowired FirestoreTemplate firestoreTemplate;

  @Autowired TestRestTemplate restTemplate;

  private TestUserClient testUserClient;

  @BeforeEach
  void cleanupEnvironment() {
    testUserClient = new TestUserClient(restTemplate.getRestTemplate());
    firestoreTemplate.deleteAll(User.class).block();
  }

  @Test
  void saveUserTest() {
    testUserClient.removePhonesForUser("Alpha");
    List<User> users = testUserClient.listUsers();
    assertThat(users).isEmpty();

    testUserClient.saveUser(ALPHA_USER, ALPHA_PHONE_NUMBERS);
    testUserClient.saveUser(BETA_USER, emptyList());
    testUserClient.saveUser(DELTA_USER, emptyList());
    User savedUser = testUserClient.saveUser(NULL_NAME_USER, emptyList());
    //ensures that a user saved with null id has an id assigned by firestore
    assertThat(savedUser.getName()).isNotNull();

    List<User> allUsers = testUserClient.listUsers();
    assertThat(allUsers).map(User::getName)
        .containsExactlyInAnyOrder("Alpha", "Beta", "Delta", savedUser.getName());

    List<User> users49 = testUserClient.findUsersByAge(49);
    assertThat(users49).containsExactlyInAnyOrder(ALPHA_USER, DELTA_USER);
    List<PhoneNumber> phoneNumbers = testUserClient.listPhoneNumbers("Alpha");
    assertThat(phoneNumbers)
        .map(PhoneNumber::getNumber)
        .containsExactlyInAnyOrder("555666777", "777666555");

    testUserClient.removeUserByName("Alpha");
    phoneNumbers = testUserClient.listPhoneNumbers("Alpha");
    assertThat(phoneNumbers)
        .map(PhoneNumber::getNumber)
        .containsExactlyInAnyOrder("555666777", "777666555");

    testUserClient.removePhonesForUser("Alpha");
    phoneNumbers = testUserClient.listPhoneNumbers("Alpha");
    assertThat(phoneNumbers).isEmpty();
  }
}
