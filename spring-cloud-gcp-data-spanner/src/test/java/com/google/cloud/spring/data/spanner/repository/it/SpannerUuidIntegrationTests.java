/*
 * Copyright 2026 the original author or authors.
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

package com.google.cloud.spring.data.spanner.repository.it;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.spring.data.spanner.test.AbstractSpannerIntegrationTest;
import com.google.cloud.spring.data.spanner.test.domain.UuidStringUser;
import com.google.cloud.spring.data.spanner.test.domain.UuidStringUserRepository;
import com.google.cloud.spring.data.spanner.test.domain.UuidUser;
import com.google.cloud.spring.data.spanner.test.domain.UuidUserRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@EnabledIfSystemProperty(named = "it.spanner", matches = "true")
@ExtendWith(SpringExtension.class)
public class SpannerUuidIntegrationTests extends AbstractSpannerIntegrationTest {

  @Autowired UuidUserRepository uuidUserRepository;
  @Autowired UuidStringUserRepository uuidStringUserRepository;

  @BeforeEach
  void setUp() {
    uuidUserRepository.deleteAll();
    uuidStringUserRepository.deleteAll();
  }

  @AfterEach
  void tearDown() {
    uuidUserRepository.deleteAll();
    uuidStringUserRepository.deleteAll();
  }

  @Test
  void testUuidUntypedBindingWorks() {
    UUID uuid = UUID.randomUUID();
    UuidUser user = new UuidUser(uuid, "Test User Untyped");

    uuidUserRepository.save(user);

    // Verify read using generated query method
    List<UuidUser> foundUsers = uuidUserRepository.findByUserId(uuid);
    assertThat(foundUsers).hasSize(1).contains(user);

    // Verify read using custom query with parameter binding
    List<UuidUser> foundUsersCustom = uuidUserRepository.findByUuidCustom(uuid);
    assertThat(foundUsersCustom).hasSize(1).contains(user);
  }

  @Test
  void testUuidNullBindingWorks() {
    UUID uuid = UUID.randomUUID();
    UuidUser user = new UuidUser(uuid, "Test User Null Secondary");
    user.setSecondaryId(null);

    uuidUserRepository.save(user);

    List<UuidUser> foundUsers = uuidUserRepository.findByUserId(uuid);
    assertThat(foundUsers).hasSize(1).contains(user);
    assertThat(foundUsers.get(0).getSecondaryId()).isNull();
  }

  @Test
  void testUuidUntypedBindingWorksWithString36Column() {
    UUID uuid = UUID.randomUUID();
    UuidStringUser user = new UuidStringUser(uuid, "Test User String36");

    uuidStringUserRepository.save(user);

    // Verify read using generated query method
    List<UuidStringUser> foundUsers = uuidStringUserRepository.findByUserId(uuid);
    assertThat(foundUsers).hasSize(1).contains(user);

    // Verify read using custom query with parameter binding
    List<UuidStringUser> foundUsersCustom = uuidStringUserRepository.findByUuidCustom(uuid);
    assertThat(foundUsersCustom).hasSize(1).contains(user);
  }
}
