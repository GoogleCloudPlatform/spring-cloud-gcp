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

import java.util.Arrays;
import java.util.List;

import com.google.cloud.spring.data.firestore.FirestoreTemplate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assume.assumeThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = FirestoreSampleApplication.class)
@TestPropertySource("classpath:application-test.properties")
public class FirestoreSampleApplicationIntegrationTests {
	private static final User ALPHA_USER = new User("Alpha", 49, singletonList(new Pet("rat", "Snowflake")));
	private static final List<PhoneNumber> ALPHA_PHONE_NUMBERS = Arrays.asList(
			new PhoneNumber("555666777"),
			new PhoneNumber("777666555")
	);
	private static final User BETA_USER = new User("Beta", 23, emptyList());
	private static final User DELTA_USER = new User("Delta", 49, Arrays.asList(
			new Pet("fish", "Dory"),
			new Pet("spider", "Man")
	));

	@Autowired
	FirestoreTemplate firestoreTemplate;

	@Autowired
	TestRestTemplate restTemplate;

	private TestUserClient testUserClient;

	@BeforeClass
	public static void prepare() {
		assumeThat("Firestore Spring Data tests are "
						+ "disabled. Please use '-Dit.firestore=true' to enable them. ",
				System.getProperty("it.firestore"), is("true"));
	}

	@Before
	public void cleanupEnvironment() {
		testUserClient = new TestUserClient(restTemplate.getRestTemplate());
		firestoreTemplate.deleteAll(User.class).block();
	}

	@Test
	public void saveUserTest() {
		testUserClient.removePhonesForUser("Alpha");
		List<User> users = testUserClient.listUsers();
		assertThat(users).isEmpty();

		testUserClient.saveUser(ALPHA_USER, ALPHA_PHONE_NUMBERS);
		testUserClient.saveUser(BETA_USER, emptyList());
		testUserClient.saveUser(DELTA_USER, emptyList());

		List<User> allUsers = testUserClient.listUsers();
		assertThat(allUsers)
				.map(User::getName)
				.containsExactlyInAnyOrder("Alpha", "Beta", "Delta");

		List<User> users49 = testUserClient.findUsersByAge(49);
		assertThat(users49).containsExactlyInAnyOrder(
				ALPHA_USER,
				DELTA_USER
		);
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
		assertThat(phoneNumbers)
				.isEmpty();
	}
}
