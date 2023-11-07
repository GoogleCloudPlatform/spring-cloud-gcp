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

package com.google.cloud.spring.data.firestore.it;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.Timestamp;
import com.google.cloud.spring.data.firestore.FirestoreIntegrationTestsConfiguration;
import com.google.cloud.spring.data.firestore.SimpleFirestoreReactiveRepository;
import com.google.cloud.spring.data.firestore.entities.User;
import com.google.cloud.spring.data.firestore.entities.User.Address;
import com.google.cloud.spring.data.firestore.entities.UserRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.AopTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@EnabledIfSystemProperty(named = "it.firestore", matches = "true")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = FirestoreIntegrationTestsConfiguration.class)
class FirestoreRepositoryIntegrationTests {
  // tag::autowire[]
  @Autowired UserRepository userRepository;
  // end::autowire[]

  // tag::autowire_user_service[]
  @Autowired UserService userService;
  // end::autowire_user_service[]

  @BeforeEach
  void cleanTestEnvironment() {
    this.userRepository.deleteAll().block();
  }

  @Test
  void countTest() {
    Flux<User> users =
        Flux.fromStream(IntStream.range(1, 10).boxed()).map(n -> new User("blah-person" + n, n));

    this.userRepository.saveAll(users).blockLast();

    long count = this.userRepository.countByAgeIsGreaterThan(5).block();
    assertThat(count).isEqualTo(4);
  }

  @Test
  // tag::repository_built_in[]
  void writeReadDeleteTest() {
    List<User.Address> addresses =
        Arrays.asList(
            new User.Address("123 Alice st", "US"), new User.Address("1 Alice ave", "US"));
    User.Address homeAddress = new User.Address("10 Alice blvd", "UK");
    User alice = new User("Alice", 29, null, addresses, homeAddress);
    User bob = new User("Bob", 60);

    this.userRepository.save(alice).block();
    this.userRepository.save(bob).block();

    assertThat(this.userRepository.count().block()).isEqualTo(2);
    assertThat(this.userRepository.findAll().map(User::getName).collectList().block())
        .containsExactlyInAnyOrder("Alice", "Bob");

    User aliceLoaded = this.userRepository.findById("Alice").block();
    assertThat(aliceLoaded.getAddresses()).isEqualTo(addresses);
    assertThat(aliceLoaded.getHomeAddress()).isEqualTo(homeAddress);

    // cast to SimpleFirestoreReactiveRepository for method be reachable with Spring Boot 2.4
    SimpleFirestoreReactiveRepository repository =
        AopTestUtils.getTargetObject(this.userRepository);
    StepVerifier.create(
            repository
                .deleteAllById(Arrays.asList("Alice", "Bob"))
                .then(this.userRepository.count()))
        .expectNext(0L)
        .verifyComplete();
  }
  // end::repository_built_in[]

  @Test
  // tag::repository_part_tree[]
  void partTreeRepositoryMethodTest() {
    User u1 = new User("Cloud", 22, null, null, new Address("1 First st., NYC", "USA"));
    u1.favoriteDrink = "tea";
    User u2 =
        new User(
            "Squall",
            17,
            Arrays.asList("cat", "dog"),
            null,
            new Address("2 Second st., London", "UK"));
    u2.favoriteDrink = "wine";
    Flux<User> users = Flux.fromArray(new User[] {u1, u2});

    this.userRepository.saveAll(users).blockLast();

    assertThat(this.userRepository.count().block()).isEqualTo(2);
    assertThat(this.userRepository.findBy(PageRequest.of(0, 10)).collectList().block())
        .containsExactly(u1, u2);
    assertThat(this.userRepository.findByAge(22).collectList().block()).containsExactly(u1);
    assertThat(this.userRepository.findByAgeNot(22).collectList().block()).containsExactly(u2);
    assertThat(this.userRepository.findByHomeAddressCountry("USA").collectList().block())
        .containsExactly(u1);
    assertThat(this.userRepository.findByFavoriteDrink("wine").collectList().block())
        .containsExactly(u2);
    assertThat(this.userRepository.findByAgeGreaterThanAndAgeLessThan(20, 30).collectList().block())
        .containsExactly(u1);
    assertThat(this.userRepository.findByAgeGreaterThan(10).collectList().block())
        .containsExactlyInAnyOrder(u1, u2);
    assertThat(this.userRepository.findByNameAndAge("Cloud", 22).collectList().block())
        .containsExactly(u1);
    assertThat(
            this.userRepository
                .findByNameAndPetsContains("Squall", Collections.singletonList("cat"))
                .collectList()
                .block())
        .containsExactly(u2);
  }
  // end::repository_part_tree[]

  @Test
  void pageableQueryTest() {
    Flux<User> users =
        Flux.fromStream(IntStream.range(1, 11).boxed()).map(n -> new User("blah-person" + n, n));
    this.userRepository.saveAll(users).blockLast();

    PageRequest pageRequest = PageRequest.of(2, 3, Sort.by(Order.desc("age")));
    List<String> pagedUsers =
        this.userRepository
            .findByAgeGreaterThan(0, pageRequest)
            .map(User::getName)
            .collectList()
            .block();

    assertThat(pagedUsers)
        .containsExactlyInAnyOrder("blah-person4", "blah-person3", "blah-person2");
  }

  @Test
  void sortQueryTest() {
    Flux<User> users =
        Flux.fromStream(IntStream.range(1, 11).boxed()).map(n -> new User("blah-person" + n, n));
    this.userRepository.saveAll(users).blockLast();

    List<String> pagedUsers =
        this.userRepository
            .findByAgeGreaterThan(7, Sort.by(Order.asc("age")))
            .map(User::getName)
            .collectList()
            .block();

    assertThat(pagedUsers)
        .containsExactlyInAnyOrder("blah-person8", "blah-person9", "blah-person10");
  }

  @Test
  void testOrderBy() {
    User alice = new User("Alice", 99);
    User bob = new User("Bob", 99);
    User zelda = new User("Zelda", 99);
    User claire = new User("Claire", 80);
    User dave = new User("Dave", 70);

    Flux<User> users = Flux.fromArray(new User[] {alice, bob, zelda, claire, dave});
    this.userRepository.saveAll(users).blockLast();

    StepVerifier.create(
            this.userRepository.findByAge(99, Sort.by(Order.desc("name"))).map(User::getName))
        .expectNext("Zelda", "Bob", "Alice")
        .verifyComplete();
    StepVerifier.create(this.userRepository.findByAgeOrderByNameDesc(99).map(User::getName))
        .expectNext("Zelda", "Bob", "Alice")
        .verifyComplete();
    StepVerifier.create(this.userRepository.findAllByOrderByAge().map(User::getName))
        .expectNext("Dave", "Claire", "Alice", "Bob", "Zelda")
        .verifyComplete();
  }

  @Test
  void inFilterQueryTest() {
    User u1 = new User("Cloud", 22);
    User u2 = new User("Squall", 17);
    Flux<User> users = Flux.fromArray(new User[] {u1, u2});

    this.userRepository.saveAll(users).blockLast();

    List<String> pagedUsers =
        this.userRepository
            .findByAgeIn(Arrays.asList(22, 23, 24))
            .map(User::getName)
            .collectList()
            .block();

    assertThat(pagedUsers).containsExactly("Cloud");

    pagedUsers =
        this.userRepository
            .findByAgeIn(Arrays.asList(17, 22))
            .map(User::getName)
            .collectList()
            .block();

    assertThat(pagedUsers).containsExactly("Cloud", "Squall");

    pagedUsers =
        this.userRepository
            .findByAgeIn(Arrays.asList(18, 23))
            .map(User::getName)
            .collectList()
            .block();

    assertThat(pagedUsers).isEmpty();

    pagedUsers =
        this.userRepository
            .findByAgeNotIn(Arrays.asList(17, 22, 33))
            .map(User::getName)
            .collectList()
            .block();

    assertThat(pagedUsers).isEmpty();

    pagedUsers =
        this.userRepository
            .findByAgeNotIn(Arrays.asList(10, 20, 30))
            .map(User::getName)
            .collectList()
            .block();

    assertThat(pagedUsers).containsExactlyInAnyOrder("Cloud", "Squall");

    pagedUsers =
        this.userRepository
            .findByAgeNotIn(Arrays.asList(17, 33))
            .map(User::getName)
            .collectList()
            .block();

    assertThat(pagedUsers).containsExactly("Cloud");
  }

  @Test
  void containsFilterQueryTest() {
    User u1 = new User("Cloud", 22, Arrays.asList("cat", "dog"));
    User u2 = new User("Squall", 17, Collections.singletonList("pony"));
    Flux<User> users = Flux.fromArray(new User[] {u1, u2});

    this.userRepository.saveAll(users).blockLast();

    List<String> pagedUsers =
        this.userRepository
            .findByPetsContains(Arrays.asList("cat", "dog"))
            .map(User::getName)
            .collectList()
            .block();

    assertThat(pagedUsers).containsExactly("Cloud");

    pagedUsers =
        this.userRepository
            .findByPetsContains(Arrays.asList("cat", "pony"))
            .map(User::getName)
            .collectList()
            .block();

    assertThat(pagedUsers).containsExactlyInAnyOrder("Cloud", "Squall");

    pagedUsers =
        this.userRepository
            .findByAgeAndPetsContains(17, Arrays.asList("cat", "pony"))
            .map(User::getName)
            .collectList()
            .block();

    assertThat(pagedUsers).containsExactlyInAnyOrder("Squall");

    pagedUsers =
        this.userRepository
            .findByPetsContainsAndAgeIn("cat", Arrays.asList(22, 23))
            .map(User::getName)
            .collectList()
            .block();

    assertThat(pagedUsers).containsExactlyInAnyOrder("Cloud");
  }

  @Test
  void testDoubleSub() {
    User alice = new User("Alice", 29);
    User bob = new User("Bob", 60);
    this.userRepository.save(alice).then(this.userRepository.save(bob)).block();

    Mono<User> testUser = this.userRepository.findByAge(29).next();

    Flux<String> stringFlux =
        userRepository
            .findAll()
            .flatMap(
                user -> testUser.flatMap(user1 -> Mono.just(user.getName() + " " + user1.getName())));
    List<String> list = stringFlux.collectList().block();
    assertThat(list).contains("Alice Alice", "Bob Alice");
  }

  @Test
  void testUpdateTimeNoDocumentChangeDoesNotResultInOptimisticLockingFailure() {
    User user = new User();
    user.setName("Axle");
    user.setAge(25);
    userRepository.save(user).block();
    Timestamp updateTime = user.getUpdateTime();
    userRepository.save(user).block();
    Timestamp updateTime2 = user.getUpdateTime();
    assertThat(updateTime2).isEqualTo(updateTime);
    user.setAge(26);
    userRepository.save(user).block();
    // no optimistic locking exception expected
  }
}
