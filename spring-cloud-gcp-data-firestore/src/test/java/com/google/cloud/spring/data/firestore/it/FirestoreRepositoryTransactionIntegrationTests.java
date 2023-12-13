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

import com.google.cloud.spring.data.firestore.FirestoreTransactionIntegrationTestsConfiguration;
import com.google.cloud.spring.data.firestore.entities.User;
import com.google.cloud.spring.data.firestore.entities.UserRepository;
import com.google.cloud.spring.data.firestore.transaction.ReactiveFirestoreTransactionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledInNativeImage;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import reactor.core.publisher.Mono;

@EnabledIfSystemProperty(named = "it.firestore", matches = "true")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = FirestoreTransactionIntegrationTestsConfiguration.class)
@DisabledInNativeImage
@DisabledInAotMode
class FirestoreRepositoryTransactionIntegrationTests {
  // tag::autowire[]
  @Autowired UserRepository userRepository;
  // end::autowire[]

  // tag::autowire_tx_manager[]
  @Autowired ReactiveFirestoreTransactionManager txManager;
  // end::autowire_tx_manager[]

  // tag::autowire_user_service[]
  @Autowired UserService userService;
  // end::autowire_user_service[]

  @Autowired ReactiveFirestoreTransactionManager transactionManager;

  @BeforeEach
  void cleanTestEnvironment() {
    this.userRepository.deleteAll().block();
    reset(this.transactionManager);
  }

  @Test
  void transactionalOperatorTest() {
    // tag::repository_transactional_operator[]
    DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
    transactionDefinition.setReadOnly(false);
    TransactionalOperator operator =
        TransactionalOperator.create(this.txManager, transactionDefinition);
    // end::repository_transactional_operator[]

    // tag::repository_operations_in_a_transaction[]
    User alice = new User("Alice", 29);
    User bob = new User("Bob", 60);

    this.userRepository
        .save(alice)
        .then(this.userRepository.save(bob))
        .as(operator::transactional)
        .block();

    this.userRepository
        .findAll()
        .flatMap(
            a -> {
              a.setAge(a.getAge() - 1);
              return this.userRepository.save(a);
            })
        .as(operator::transactional)
        .collectList()
        .block();

    assertThat(this.userRepository.findAll().map(User::getAge).collectList().block())
        .containsExactlyInAnyOrder(28, 59);
    // end::repository_operations_in_a_transaction[]
  }

  @Test
  void declarativeTransactionRollbackTest() {
    this.userService.deleteUsers().onErrorResume(throwable -> Mono.empty()).block();

    verify(this.transactionManager, times(0)).commit(any());
    verify(this.transactionManager, times(1)).rollback(any());
    verify(this.transactionManager, times(1)).getReactiveTransaction(any());
  }

  @Test
  void declarativeTransactionCommitTest() {
    User alice = new User("Alice", 29);
    User bob = new User("Bob", 60);

    this.userRepository.save(alice).then(this.userRepository.save(bob)).block();

    this.userService.updateUsers().block();

    verify(this.transactionManager, times(1)).commit(any());
    verify(this.transactionManager, times(0)).rollback(any());
    verify(this.transactionManager, times(1)).getReactiveTransaction(any());

    assertThat(this.userRepository.findAll().map(User::getAge).collectList().block())
        .containsExactlyInAnyOrder(28, 59);
  }

  @Test
  void transactionPropagationTest() {
    User alice = new User("Alice", 29);
    User bob = new User("Bob", 60);

    this.userRepository.save(alice).then(this.userRepository.save(bob)).block();

    this.userService.updateUsersTransactionPropagation().block();

    verify(this.transactionManager, times(1)).commit(any());
    verify(this.transactionManager, times(0)).rollback(any());
    verify(this.transactionManager, times(1)).getReactiveTransaction(any());

    assertThat(this.userRepository.findAll().map(User::getAge).collectList().block())
        .containsExactlyInAnyOrder(28, 59);
  }
}
