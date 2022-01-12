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
import static org.hamcrest.Matchers.is;
import static org.junit.Assume.assumeThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.NoCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.FirestoreEmulatorContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

/**
 * Tests created native docker image when maven "native" profile is active. A firestore is run in
 * docker using TestContainers, and then our app's docker image will connect to that firestore to
 * persist/fetch data. Test scenario is almost exactly the same as {@link
 * FirestoreSampleApplicationIntegrationTests}.
 */
@Testcontainers
@Tag("native")
public class FirestoreSampleApplicationNativeIntegrationTests {
  private static final User ALPHA_USER =
      new User("Alpha", 49, singletonList(new Pet("rat", "Snowflake")));
  private static final List<PhoneNumber> ALPHA_PHONE_NUMBERS =
      Arrays.asList(new PhoneNumber("555666777"), new PhoneNumber("777666555"));
  private static final User BETA_USER = new User("Beta", 23, emptyList());
  private static final User DELTA_USER =
      new User("Delta", 49, Arrays.asList(new Pet("fish", "Dory"), new Pet("spider", "Man")));
  private static final String MY_GCP_PROJECT_ID = "my-gcp-project-id";

  @Container
  private static final FirestoreEmulatorContainer emulator =
      new FirestoreEmulatorContainer(
          DockerImageName.parse("gcr.io/google.com/cloudsdktool/cloud-sdk:316.0.0-emulators"));

  private static Firestore firestore;

  @BeforeAll
  static void beforeAll() {
    assumeThat(
        "Firestore Native tests are " + "disabled. Please use '-Dit.native=true' to enable them. ",
        System.getProperty("it.native"),
        is("true"));

    FirestoreOptions options =
        FirestoreOptions.getDefaultInstance().toBuilder()
            .setHost(emulator.getEmulatorEndpoint())
            .setCredentials(NoCredentials.getInstance())
            .setProjectId(MY_GCP_PROJECT_ID)
            .build();
    firestore = options.getService();
  }

  @Test
  void testApplicationNativeDockerImage() throws ExecutionException, InterruptedException {
    // expose firestore port from local machine to be used by our app's docker image
    Integer firestorePort = emulator.getMappedPort(8080);
    org.testcontainers.Testcontainers.exposeHostPorts(firestorePort);

    try (MyAppNativeDockerContainer myApp = new MyAppNativeDockerContainer(emulator)) {
      myApp.start();
      RestTemplate restTemplate = new RestTemplateBuilder().rootUri(myApp.getBaseUrl()).build();
      TestUserClient testUserClient = new TestUserClient(restTemplate);

      testUserClient.removePhonesForUser("Alpha");
      List<User> allUsers = testUserClient.listUsers();
      assertThat(allUsers).isEmpty();

      testUserClient.saveUser(ALPHA_USER, ALPHA_PHONE_NUMBERS);
      verifyUserPersisted(ALPHA_USER);

      testUserClient.saveUser(BETA_USER, emptyList());
      verifyUserPersisted(BETA_USER);

      testUserClient.saveUser(DELTA_USER, emptyList());
      verifyUserPersisted(DELTA_USER);

      allUsers = testUserClient.listUsers();
      assertThat(allUsers).map(User::getName).containsExactlyInAnyOrder("Alpha", "Beta", "Delta");

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

  private void verifyUserPersisted(User user) throws InterruptedException, ExecutionException {
    CollectionReference users = firestore.collection("users");
    DocumentReference docRef = users.document(user.getName());
    Map<String, Object> storedDoc = docRef.get().get().getData();

    assertThat(storedDoc).isNotNull().containsEntry("age", (long) user.getAge());
    List<Map<String, Object>> petsMapList =
        new ObjectMapper()
            .convertValue(user.getPets(), new TypeReference<List<Map<String, Object>>>() {});
    assertThat(storedDoc.get("pets"))
        .isNotNull()
        .asList()
        .hasSize(user.getPets().size())
        .containsAll(petsMapList);
  }

  private static class MyAppNativeDockerContainer
      extends GenericContainer<MyAppNativeDockerContainer> {
    public static final int APP_PORT = 8080;

    MyAppNativeDockerContainer(FirestoreEmulatorContainer emulator) {
      super(DockerImageName.parse("spring-cloud-gcp-data-firestore-sample:test"));
      dependsOn(emulator);
      withEnv("SPRING_CLOUD_GCP_FIRESTORE_EMULATOR_ENABLED", "true");
      withEnv(
          "SPRING_CLOUD_GCP_FIRESTORE_HOST_PORT",
          "host.testcontainers.internal:" + emulator.getMappedPort(8080));
      withEnv("SPRING_CLOUD_GCP_FIRESTORE_PROJECT_ID", MY_GCP_PROJECT_ID);
      withExposedPorts(8080);
      waitingFor(Wait.forHttp("/"));
      withLogConsumer(
          outputFrame ->
              System.out.println("MYAPP-DOCKER-OUTPUT  " + outputFrame.getUtf8String().trim()));
    }

    public String getBaseUrl() {
      return "http://" + getHost() + ":" + getMappedPort(APP_PORT);
    }
  }
}
