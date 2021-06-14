/*
 * Copyright 2021-2021 the original author or authors.
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

package com.google.cloud.spring.data.firestore.mapping;

import com.google.cloud.firestore.annotation.DocumentId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.model.Property;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.util.ClassTypeInformation;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link FirestorePersistentPropertyImpl}
 */
@RunWith(MockitoJUnitRunner.class)
public class FirestorePersistentPropertyImplTest {

    @Mock
    Property mockProperty;
    @Mock
    PersistentEntity mockPersistentEntity;
    @Mock
    SimpleTypeHolder mockSimpleTypeHolder;


    @Test
    public void testGetFieldName_isIdProperty() throws NoSuchFieldException {
        when(mockProperty.getName()).thenReturn("id");
        when(mockProperty.getField()).thenReturn(Optional.of(TestEntity.class.getField("id")));
        when(mockPersistentEntity.getTypeInformation()).thenReturn(ClassTypeInformation.from(TestEntity.class));
        when(mockPersistentEntity.getType()).thenReturn(TestEntity.class);

        FirestorePersistentPropertyImpl firestorePersistentProperty = new FirestorePersistentPropertyImpl(mockProperty,
                mockPersistentEntity, mockSimpleTypeHolder);
        assertThat(firestorePersistentProperty.getFieldName()).isEqualTo("__name__");
    }

    static class TestEntity {
        @DocumentId
        public String id;
    }
}
