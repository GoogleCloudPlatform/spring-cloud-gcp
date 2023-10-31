/*
 * Copyright 2017-2022 the original author or authors.
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

import com.google.cloud.spring.data.datastore.core.DatastoreTemplate;
import java.util.concurrent.TimeUnit;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/** Tests for the Book Shelf sample app. */
// Please use "-Dit.datastore=true" to enable the tests.
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = DatastoreBookshelfExample.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnabledIfSystemProperty(named = "it.datastore", matches = "true")
class DatastoreBookshelfExampleIntegrationTests {

  @Autowired private DatastoreTemplate datastoreTemplate;

  @Autowired private TestRestTemplate restTemplate;

  @BeforeEach
  void saveBooks() {
    sendRequest(
        "/saveBook",
        "{\"id\":12345678, \"title\":\"The Moon Is a Harsh Mistress\", \"author\":\"Robert A. Heinlein\", \"year\":1966}",
        HttpMethod.POST);
    sendRequest(
        "/saveBook",
        "{\"title\":\"Stranger in a Strange Land\", \"author\":\"Robert A. Heinlein\", \"year\":1961}",
        HttpMethod.POST);
    sendRequest(
        "/saveBook",
        "{\"title\":\"The Crack in Space\", \"author\":\"Philip K. Dick\", \"year\":1966}",
        HttpMethod.POST);
    sendRequest(
        "/saveBook",
        "{\"title\":\"Ubik\", \"author\":\"Philip K. Dick\", \"year\":1969}",
        HttpMethod.POST);
  }

  @AfterEach
  void cleanUp() {
    this.datastoreTemplate.deleteAll(Book.class);
  }

  @Test
  void testSerializedPage() {
    String responseBody = sendRequest("/allbooksserialized", null, HttpMethod.GET);
    assertThat(responseBody)
        .contains("content\":[{\"id\":12345678}]")
        .containsPattern("\"pageable\":.+\"urlSafeCursor\":\".+\"");
  }

  @Test
  void findAllBooksTest() {
    Awaitility.await().atMost(15, TimeUnit.SECONDS).untilAsserted(() -> {
      String responseBody = sendRequest("/findAllBooks", null, HttpMethod.GET);
      assertThat(responseBody)
          .contains("title='The Moon Is a Harsh Mistress', author='Robert A. Heinlein', year=1966")
          .contains("title='Stranger in a Strange Land', author='Robert A. Heinlein', year=1961")
          .contains("title='The Crack in Space', author='Philip K. Dick', year=1966")
          .contains("title='Ubik', author='Philip K. Dick', year=1969");
    });
  }

  @Test
  void findByAuthorTest() {
    Awaitility.await().atMost(15, TimeUnit.SECONDS).untilAsserted(() -> {
      String responseBody = sendRequest("/findByAuthor?author=Robert A. Heinlein", null, HttpMethod.GET);
      assertThat(responseBody)
          .contains("title='The Moon Is a Harsh Mistress', author='Robert A. Heinlein', year=1966")
          .contains("title='Stranger in a Strange Land', author='Robert A. Heinlein', year=1961")
          .doesNotContain("title='The Crack in Space', author='Philip K. Dick', year=1966")
          .doesNotContain("title='Ubik', author='Philip K. Dick', year=1969");
    });
  }

  @Test
  void findByYearGreaterThanTest() {
    Awaitility.await().atMost(15, TimeUnit.SECONDS).untilAsserted(() -> {
      String responseBody = sendRequest("/findByYearGreaterThan?year=1967", null, HttpMethod.GET);
      assertThat(responseBody)
          .doesNotContain("title='The Moon Is a Harsh Mistress', author='Robert A. Heinlein', year=1966")
          .doesNotContain("title='Stranger in a Strange Land', author='Robert A. Heinlein', year=1961")
          .doesNotContain("title='The Crack in Space', author='Philip K. Dick', year=1966")
          .contains("title='Ubik', author='Philip K. Dick', year=1969");
    });
  }

  @Test
  void findByAuthorYearTest() {
    Awaitility.await().atMost(15, TimeUnit.SECONDS).untilAsserted(() -> {
      String responseBody = sendRequest("/findByAuthorYear?author=Philip K. Dick&year=1966", null, HttpMethod.GET);
      assertThat(responseBody)
          .doesNotContain("title='The Moon Is a Harsh Mistress', author='Robert A. Heinlein', year=1966")
          .doesNotContain("title='Stranger in a Strange Land', author='Robert A. Heinlein', year=1961")
          .contains("title='The Crack in Space', author='Philip K. Dick', year=1966")
          .doesNotContain("title='Ubik', author='Philip K. Dick', year=1969");
    });
  }

  @Test
  void removeAllBooksTest() {
    sendRequest("/removeAllBooks", null, HttpMethod.DELETE);
    Awaitility.await().atMost(15, TimeUnit.SECONDS).untilAsserted(() -> {
      String responseBody = sendRequest("/findAllBooks", null, HttpMethod.GET);
      assertThat(responseBody)
          .doesNotContain("title='The Moon Is a Harsh Mistress', author='Robert A. Heinlein', year=1966")
          .doesNotContain("title='Stranger in a Strange Land', author='Robert A. Heinlein', year=1961")
          .doesNotContain("title='The Crack in Space', author='Philip K. Dick', year=1966")
          .doesNotContain("title='Ubik', author='Philip K. Dick', year=1969");
    });
  }

  private String sendRequest(String url, String json, HttpMethod method) {
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("Content-Type", "application/json");

    HttpEntity<String> entity = new HttpEntity<>(json, map);
    ResponseEntity<String> response = this.restTemplate.exchange(url, method, entity, String.class);
    return response.getBody();
  }
}
