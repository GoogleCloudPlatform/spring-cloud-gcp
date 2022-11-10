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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/** Helper class to call REST API from sample application using project data model classes. */
public class TestUserClient {

  private final RestTemplate restTemplate;

  public TestUserClient(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public List<User> listUsers() {
    User[] users = restTemplate.getForObject("/users", User[].class);
    assertThat(users).isNotNull();
    return Arrays.asList(users);
  }

  public List<User> findUsersByAge(int age) {
    User[] users = restTemplate.getForObject("/users/age?age=" + age, User[].class);
    assertThat(users).isNotNull();
    return Arrays.asList(users);
  }

  public List<PhoneNumber> listPhoneNumbers(String name) {
    PhoneNumber[] phoneNumbers =
        restTemplate.getForObject("/users/phones?name=" + name, PhoneNumber[].class);
    assertThat(phoneNumbers).isNotNull();
    return Arrays.asList(phoneNumbers);
  }

  public void removeUserByName(String name) {
    ResponseEntity<String> response =
        restTemplate.getForEntity("/users/removeUser?name=" + name, String.class);
    assertThat(response.getStatusCode()).isSameAs(HttpStatus.OK);
  }

  public void removePhonesForUser(String name) {
    restTemplate.getForEntity("/users/removePhonesForUser?name=" + name, String.class);
  }

  public User saveUser(User user, List<PhoneNumber> phoneNumbers) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

    MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    map.add("name", user.getName());
    map.add("age", user.getAge());
    map.add(
        "pets",
        user.getPets().stream()
            .map(pet -> pet.getType() + "-" + pet.getName())
            .collect(Collectors.joining(",")));
    map.add(
        "phones",
        phoneNumbers.stream().map(PhoneNumber::getNumber).collect(Collectors.joining(",")));

    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
    return this.restTemplate.postForObject("/users/saveUser", request, User.class);
  }
}
