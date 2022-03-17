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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ch.qos.logback.classic.Level;
import com.google.cloud.Timestamp;
import com.google.cloud.spring.data.firestore.FirestoreDataException;
import com.google.cloud.spring.data.firestore.FirestoreReactiveOperations;
import com.google.cloud.spring.data.firestore.FirestoreTemplate;
import com.google.cloud.spring.data.firestore.entities.PhoneNumber;
import com.google.cloud.spring.data.firestore.entities.User;
import com.google.cloud.spring.data.firestore.transaction.ReactiveFirestoreTransactionManager;
import io.grpc.StatusRuntimeException;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@EnabledIfSystemProperty(named = "it.firestore", matches = "true")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = FirestoreIntegrationTestsConfiguration.class)
class FirestoreIntegrationTests {

  @Autowired FirestoreTemplate firestoreTemplate;

  @Autowired ReactiveFirestoreTransactionManager txManager;

  @BeforeAll
  static void setLogger() {
    ch.qos.logback.classic.Logger root =
        (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("io.grpc.netty");
    root.setLevel(Level.INFO);
  }

  @BeforeEach
  void cleanTestEnvironment() {
    this.firestoreTemplate.deleteAll(User.class).block();
  }

  @Test
  void generateIdTest() {
    User user = new User(null, 29);
    User bob = new User("Bob", 60);

    List<User> savedUsers =
        this.firestoreTemplate.saveAll(Flux.just(user, bob)).collectList().block();
    assertThat(savedUsers).extracting("name").doesNotContainNull();

    List<User> users = this.firestoreTemplate.findAll(User.class).collectList().block();
    assertThat(users).containsExactlyInAnyOrder(savedUsers.toArray(new User[0]));
  }

  @Test
  void updateTimeTest() {
    User bob = new User("Bob", 60, null);
    this.firestoreTemplate.saveAll(Flux.just(bob)).collectList().block();

    List<User> users = this.firestoreTemplate.findAll(User.class).collectList().block();
    assertThat(users).containsExactly(bob);
    assertThat(bob.getUpdateTime()).isNotNull();
  }

  @Test
  void optimisticLockingTest() {
    User bob = new User("Bob", 60, null);

    this.firestoreTemplate.saveAll(Flux.just(bob)).collectList().block();
    Timestamp bobUpdateTime = bob.getUpdateTime();
    assertThat(bobUpdateTime).isNotNull();

    User bob2 = new User("Bob", 60, null);
    Flux<User> justBob2 = Flux.just(bob2);
    StepVerifier.create(this.firestoreTemplate.saveAll(justBob2))
        .expectErrorSatisfies(
            t -> {
              assertThat(t).isInstanceOf(StatusRuntimeException.class);
              assertThat(t).hasMessageContaining("ALREADY_EXISTS");
            })
        .verify();

    bob.setAge(15);
    this.firestoreTemplate.saveAll(Flux.just(bob)).collectList().block();
    assertThat(bob.getUpdateTime()).isGreaterThan(bobUpdateTime);

    List<User> users = this.firestoreTemplate.findAll(User.class).collectList().block();
    assertThat(users).containsExactly(bob);

    User bob3 = users.get(0);
    bob3.setAge(20);
    this.firestoreTemplate.saveAll(Flux.just(bob3)).collectList().block();

    Flux<User> justBob = Flux.just(bob);
    StepVerifier.create(this.firestoreTemplate.saveAll(justBob))
        .expectErrorSatisfies(
            t -> {
              assertThat(t).isInstanceOf(StatusRuntimeException.class);
              assertThat(t).hasMessageContaining("does not match the required base version");
            })
        .verify();
  }

  @Test
  void optimisticLockingTransactionTest() {
    User bob = new User("Bob", 60, null);

    TransactionalOperator operator = TransactionalOperator.create(txManager);

    this.firestoreTemplate.saveAll(Flux.just(bob)).collectList().block();
    Timestamp bobUpdateTime = bob.getUpdateTime();
    assertThat(bobUpdateTime).isNotNull();

    User bob2 = new User("Bob", 60, null);

    this.firestoreTemplate
        .saveAll(Flux.just(bob2))
        .collectList()
        .as(operator::transactional)
        .as(StepVerifier::create)
        .expectError()
        .verify();

    bob.setAge(15);
    this.firestoreTemplate
        .saveAll(Flux.just(bob))
        .as(operator::transactional)
        .collectList()
        .block();
    assertThat(bob.getUpdateTime()).isGreaterThan(bobUpdateTime);

    List<User> users = this.firestoreTemplate.findAll(User.class).collectList().block();
    assertThat(users).containsExactly(bob);

    User bob3 = users.get(0);
    bob3.setAge(20);
    this.firestoreTemplate
        .saveAll(Flux.just(bob3))
        .as(operator::transactional)
        .collectList()
        .block();

    this.firestoreTemplate
        .saveAll(Flux.just(bob))
        .as(operator::transactional)
        .collectList()
        .as(operator::transactional)
        .as(StepVerifier::create)
        .expectError()
        .verify();
  }

  @Test
  void transactionTest() {
    User alice = new User("Alice", 29);
    User bob = new User("Bob", 60);

    User user = new User(null, 40);

    DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
    transactionDefinition.setReadOnly(false);
    TransactionalOperator operator =
        TransactionalOperator.create(this.txManager, transactionDefinition);

    reset(this.txManager);

    this.firestoreTemplate
        .save(alice)
        .then(this.firestoreTemplate.save(bob))
        .then(this.firestoreTemplate.save(user))
        .as(operator::transactional)
        .block();

    assertThat(this.firestoreTemplate.findAll(User.class).collectList().block())
        .containsExactlyInAnyOrder(bob, alice, user);

    verify(this.txManager, times(1)).commit(any());
    verify(this.txManager, times(0)).rollback(any());
    verify(this.txManager, times(1)).getReactiveTransaction(any());

    reset(this.txManager);

    // test rollback
    this.firestoreTemplate
        .saveAll(
            Mono.defer(
                () -> {
                  throw new FirestoreDataException("BOOM!");
                }))
        .then(this.firestoreTemplate.deleteAll(User.class))
        .as(operator::transactional)
        .onErrorResume(throwable -> Mono.empty())
        .block();

    verify(this.txManager, times(0)).commit(any());
    verify(this.txManager, times(1)).rollback(any());
    verify(this.txManager, times(1)).getReactiveTransaction(any());

    assertThat(this.firestoreTemplate.count(User.class).block()).isEqualTo(3);

    this.firestoreTemplate
        .findAll(User.class)
        .flatMap(
            a -> {
              a.setAge(a.getAge() - 1);
              return this.firestoreTemplate.save(a);
            })
        .as(operator::transactional)
        .collectList()
        .block();

    List<User> users = this.firestoreTemplate.findAll(User.class).collectList().block();
    assertThat(users).extracting("age").containsExactlyInAnyOrder(28, 59, 39);
    assertThat(users).extracting("name").doesNotContainNull();

    this.firestoreTemplate.deleteAll(User.class).as(operator::transactional).block();
    assertThat(this.firestoreTemplate.findAll(User.class).collectList().block()).isEmpty();
  }

  @Test
  void writeReadDeleteTest() {
    User alice = new User("Alice", 29);
    User bob = new User("Bob", 60);

    this.firestoreTemplate.save(alice).block();
    this.firestoreTemplate.save(bob).block();

    assertThat(this.firestoreTemplate.findById(Mono.just("Saitama"), User.class).block()).isNull();

    assertThat(this.firestoreTemplate.findById(Mono.just("Bob"), User.class).block())
        .isEqualTo(bob);
    assertThat(
            this.firestoreTemplate
                .findAllById(Flux.just("Bob", "Saitama", "Alice"), User.class)
                .collectList()
                .block())
        .containsExactlyInAnyOrder(bob, alice);

    List<User> usersBeforeDelete = this.firestoreTemplate.findAll(User.class).collectList().block();
    assertThat(usersBeforeDelete).containsExactlyInAnyOrder(alice, bob);

    assertThat(this.firestoreTemplate.count(User.class).block()).isEqualTo(2);
    this.firestoreTemplate.delete(Mono.just(bob)).block();
    assertThat(this.firestoreTemplate.count(User.class).block()).isEqualTo(1);
    assertThat(this.firestoreTemplate.existsById(Mono.just("Alice"), User.class).block())
        .isEqualTo(Boolean.TRUE);
    assertThat(this.firestoreTemplate.existsById(Mono.just("Bob"), User.class).block())
        .isEqualTo(Boolean.FALSE);
    this.firestoreTemplate.deleteById(Mono.just("Alice"), User.class).block();
    assertThat(this.firestoreTemplate.count(User.class).block()).isZero();

    alice.setUpdateTime(null);
    this.firestoreTemplate.save(alice).block();
    bob.setUpdateTime(null);
    this.firestoreTemplate.save(bob).block();

    assertThat(this.firestoreTemplate.deleteAll(User.class).block()).isEqualTo(2);
    assertThat(this.firestoreTemplate.findAll(User.class).collectList().block()).isEmpty();

    // tag::subcollection[]
    FirestoreReactiveOperations bobTemplate =
        this.firestoreTemplate.withParent(new User("Bob", 60));

    PhoneNumber phoneNumber = new PhoneNumber("111-222-333");
    bobTemplate.save(phoneNumber).block();
    assertThat(bobTemplate.findAll(PhoneNumber.class).collectList().block())
        .containsExactly(phoneNumber);
    bobTemplate.deleteAll(PhoneNumber.class).block();
    assertThat(bobTemplate.findAll(PhoneNumber.class).collectList().block()).isEmpty();
    // end::subcollection[]
  }

  @Test
  void deleteAllByIdTest() {
    User alice = new User("Alice", 29);
    User bob = new User("Bob", 60);

    this.firestoreTemplate.save(alice).block();
    this.firestoreTemplate.save(bob).block();

    StepVerifier.create(
            this.firestoreTemplate
                .deleteById(Flux.just("Bob", "Saitama", "Alice"), User.class)
                .then(this.firestoreTemplate.count(User.class)))
        .expectNext(0L)
        .verifyComplete();
  }

  @Test
  void saveTest() {
    assertThat(this.firestoreTemplate.count(User.class).block()).isZero();

    User u1 = new User("Cloud", 22);
    this.firestoreTemplate.save(u1).block();

    assertThat(this.firestoreTemplate.findAll(User.class).collectList().block())
        .containsExactly(u1);
  }

  @Test
  void saveAllTest() {
    User u1 = new User("Cloud", 22);
    User u2 = new User("Squall", 17);
    Flux<User> users = Flux.fromArray(new User[] {u1, u2});

    assertThat(this.firestoreTemplate.count(User.class).block()).isZero();

    this.firestoreTemplate.saveAll(users).blockLast();

    assertThat(this.firestoreTemplate.count(User.class).block()).isEqualTo(2);
    assertThat(this.firestoreTemplate.deleteAll(User.class).block()).isEqualTo(2);
  }

  @Test
  void saveAllBulkTest() {
    int numEntities = 1000;
    Flux<User> users =
        Flux.create(
            sink -> {
              for (int i = 0; i < numEntities; i++) {
                sink.next(new User(UUID.randomUUID().toString(), i));
              }
              sink.complete();
            });

    assertThat(this.firestoreTemplate.findAll(User.class).collectList().block()).isEmpty();

    this.firestoreTemplate.saveAll(users).blockLast();

    assertThat(this.firestoreTemplate.findAll(User.class).count().block()).isEqualTo(numEntities);
  }

  @Test
  void deleteTest() {
    this.firestoreTemplate.save(new User("alpha", 45)).block();
    this.firestoreTemplate.save(new User("beta", 23)).block();
    this.firestoreTemplate.save(new User("gamma", 44)).block();
    this.firestoreTemplate.save(new User("Joe Hogan", 22)).block();

    assertThat(this.firestoreTemplate.count(User.class).block()).isEqualTo(4);

    this.firestoreTemplate.delete(Mono.just(new User("alpha", 45))).block();
    assertThat(this.firestoreTemplate.findAll(User.class).map(User::getName).collectList().block())
        .containsExactlyInAnyOrder("beta", "gamma", "Joe Hogan");

    this.firestoreTemplate.deleteById(Mono.just("beta"), User.class).block();
    assertThat(this.firestoreTemplate.findAll(User.class).map(User::getName).collectList().block())
        .containsExactlyInAnyOrder("gamma", "Joe Hogan");

    this.firestoreTemplate.deleteAll(User.class).block();
    assertThat(this.firestoreTemplate.count(User.class).block()).isZero();
  }
}
