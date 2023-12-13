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

package com.google.cloud.spring.data.firestore.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import ch.qos.logback.classic.Level;
import com.google.cloud.Timestamp;
import com.google.cloud.spring.data.firestore.FirestoreDataException;
import com.google.cloud.spring.data.firestore.FirestoreTemplate;
import com.google.cloud.spring.data.firestore.FirestoreTransactionIntegrationTestsConfiguration;
import com.google.cloud.spring.data.firestore.entities.User;
import com.google.cloud.spring.data.firestore.transaction.ReactiveFirestoreTransactionManager;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@EnabledIfSystemProperty(named = "it.firestore", matches = "true")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = FirestoreTransactionIntegrationTestsConfiguration.class)
@DisabledInNativeImage
@DisabledInAotMode
class FirestoreTransactionIntegrationTests {

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
}
