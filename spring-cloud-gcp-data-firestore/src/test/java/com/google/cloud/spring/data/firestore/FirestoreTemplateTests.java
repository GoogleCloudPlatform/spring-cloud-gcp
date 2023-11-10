/*
 * Copyright 2019-2019 the original author or authors.
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

package com.google.cloud.spring.data.firestore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.mapping.FirestoreDefaultClassMapper;
import com.google.cloud.spring.data.firestore.mapping.FirestoreMappingContext;
import com.google.cloud.spring.data.firestore.mapping.FirestorePersistentEntityImpl;
import com.google.cloud.spring.data.firestore.mapping.UpdateTime;
import com.google.firestore.v1.CommitRequest;
import com.google.firestore.v1.CommitResponse;
import com.google.firestore.v1.Document.Builder;
import com.google.firestore.v1.DocumentMask;
import com.google.firestore.v1.FirestoreGrpc.FirestoreStub;
import com.google.firestore.v1.GetDocumentRequest;
import com.google.firestore.v1.Precondition;
import com.google.firestore.v1.RunQueryRequest;
import com.google.firestore.v1.RunQueryResponse;
import com.google.firestore.v1.StructuredQuery;
import com.google.firestore.v1.Value;
import com.google.firestore.v1.Write;
import com.google.firestore.v1.WriteResult;
import io.grpc.stub.StreamObserver;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.TypeInformation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * @since 1.2
 */
public class FirestoreTemplateTests {

  private FirestoreTemplate firestoreTemplate;

  private final FirestoreStub firestoreStub = mock(FirestoreStub.class);

  private static final String parent = "projects/my-project/databases/(default)/documents";

  @BeforeEach
  void setup() {
    FirestoreMappingContext mappingContext = new FirestoreMappingContext();
    this.firestoreTemplate =
        new FirestoreTemplate(
            this.firestoreStub,
            parent,
            new FirestoreDefaultClassMapper(mappingContext),
            mappingContext);
  }

  @Test
  void templateWithSuffixForTestTest() {
    FirestoreMappingContext mappingContext = new FirestoreMappingContext();
    FirestoreTemplate firestoreTemplateWithSuffix =
        new FirestoreTemplate(
            this.firestoreStub,
            parent,
            new FirestoreDefaultClassMapper(mappingContext),
            mappingContext,
            "_suffix");

    FirestorePersistentEntityImpl<TestEntity> persistentEntity =
        new FirestorePersistentEntityImpl<TestEntity>(TypeInformation.of(TestEntity.class));
    String name = firestoreTemplateWithSuffix.buildResourceName(persistentEntity, "resource");
    assertThat(name).isEqualTo(parent + "/testEntities_suffix/resource");
  }

  @Test
  void findAllTest() {
    mockRunQueryMethod();

    StepVerifier.create(this.firestoreTemplate.findAll(TestEntity.class))
        .expectNext(new TestEntity("e1", 100L), new TestEntity("e2", 200L))
        .verifyComplete();

    StructuredQuery structuredQuery =
        StructuredQuery.newBuilder()
            .addFrom(
                StructuredQuery.CollectionSelector.newBuilder()
                    .setCollectionId("testEntities")
                    .build())
            .build();
    RunQueryRequest request =
        RunQueryRequest.newBuilder()
            .setParent(parent)
            .setStructuredQuery(structuredQuery)
            .build();

    verify(this.firestoreStub, times(1)).runQuery(eq(request), any());
    verify(this.firestoreStub, times(1)).runQuery(any(), any());
  }

  @Test
  void saveAllTest() {
    mockCommitMethod();

    StepVerifier.create(
            this.firestoreTemplate.saveAll(
                Flux.just(new TestEntity("e1", 100L), new TestEntity("e2", 200L))))
        .expectNext(new TestEntity("e1", 100L), new TestEntity("e2", 200L))
        .verifyComplete();

    CommitRequest.Builder builder =
        CommitRequest.newBuilder().setDatabase("projects/my-project/databases/(default)");

    builder.addWrites(Write.newBuilder().setUpdate(buildDocument("e1", 100L)).build());
    builder.addWrites(Write.newBuilder().setUpdate(buildDocument("e2", 200L)).build());

    verify(this.firestoreStub, times(1)).commit(eq(builder.build()), any());
  }

  @Test
  void saveNoId() {
    mockCommitMethod();

    TestEntity entity1 = new TestEntity(null, 100L);
    TestEntity entity2 = new TestEntity(null, 200L);
    assertNull(entity1.getIdField());
    assertNull(entity2.getIdField());

    StepVerifier.create(
            this.firestoreTemplate.saveAll(
                Flux.just(entity1, entity2)))
        .assertNext(e1 -> assertNotNull(e1.getIdField()))
        .assertNext(e2 -> assertNotNull(e2.getIdField()))
        .verifyComplete();

    CommitRequest.Builder builder =
        CommitRequest.newBuilder().setDatabase("projects/my-project/databases/(default)");

    builder.addWrites(Write.newBuilder()
        .setUpdate(buildDocument(entity1.getIdField(), 100L))
        .setCurrentDocument(Precondition.newBuilder().setExists(false).build())
        .build());
    builder.addWrites(Write.newBuilder()
        .setUpdate(buildDocument(entity2.getIdField(), 200L))
        .setCurrentDocument(Precondition.newBuilder().setExists(false).build())
        .build());

    verify(this.firestoreStub, times(1)).commit(eq(builder.build()), any());
  }

  @Test
  void updateTimeVersionSaveTest() {
    mockCommitMethod();

    Timestamp expectedUpdateTime = Timestamp.ofTimeMicroseconds(123456789);
    StepVerifier.create(
            this.firestoreTemplate.saveAll(
                Flux.just(
                    new TestEntityUpdateTimeVersion("e1"), new TestEntityUpdateTimeVersion("e2"))))
        .expectNext(
            new TestEntityUpdateTimeVersion("e1", expectedUpdateTime),
            new TestEntityUpdateTimeVersion("e2", expectedUpdateTime))
        .verifyComplete();

    CommitRequest.Builder builder =
        CommitRequest.newBuilder().setDatabase("projects/my-project/databases/(default)");

    Precondition doesNotExistPrecondition = Precondition.newBuilder().setExists(false).build();
    builder.addWrites(
        Write.newBuilder()
            .setUpdate(buildDocument("e1", null))
            .setCurrentDocument(doesNotExistPrecondition)
            .build());
    builder.addWrites(
        Write.newBuilder()
            .setUpdate(buildDocument("e2", null))
            .setCurrentDocument(doesNotExistPrecondition)
            .build());

    verify(this.firestoreStub, times(1)).commit(eq(builder.build()), any());
  }

  @Test
  void updateTimeVersionUpdateTest() {
    mockCommitMethod();

    Timestamp expectedUpdateTime = Timestamp.ofTimeMicroseconds(123456789);
    Timestamp previousUpdateTimeE1 = Timestamp.ofTimeMicroseconds(987654321);
    Timestamp previousUpdateTimeE2 = Timestamp.ofTimeMicroseconds(918273645);
    StepVerifier.create(
            this.firestoreTemplate.saveAll(
                Flux.just(
                    new TestEntityUpdateTimeVersion("e1", previousUpdateTimeE1),
                    new TestEntityUpdateTimeVersion("e2", previousUpdateTimeE2))))
        .expectNext(
            new TestEntityUpdateTimeVersion("e1", expectedUpdateTime),
            new TestEntityUpdateTimeVersion("e2", expectedUpdateTime))
        .verifyComplete();

    CommitRequest.Builder builder =
        CommitRequest.newBuilder().setDatabase("projects/my-project/databases/(default)");

    Precondition preconditionE1 =
        Precondition.newBuilder().setUpdateTime(previousUpdateTimeE1.toProto()).build();
    Precondition preconditionE2 =
        Precondition.newBuilder().setUpdateTime(previousUpdateTimeE2.toProto()).build();

    builder.addWrites(
        Write.newBuilder()
            .setUpdate(buildDocument("e1", null))
            .setCurrentDocument(preconditionE1)
            .build());
    builder.addWrites(
        Write.newBuilder()
            .setUpdate(buildDocument("e2", null))
            .setCurrentDocument(preconditionE2)
            .build());

    verify(this.firestoreStub, times(1)).commit(eq(builder.build()), any());
  }

  @Test
  void updateTimeSaveTest() {
    mockCommitMethod();

    Timestamp expectedUpdateTime = Timestamp.ofTimeMicroseconds(123456789);
    StepVerifier.create(
            this.firestoreTemplate.saveAll(
                Flux.just(new TestEntityUpdateTime("e1"), new TestEntityUpdateTime("e2"))))
        .expectNext(
            new TestEntityUpdateTime("e1", expectedUpdateTime),
            new TestEntityUpdateTime("e2", expectedUpdateTime))
        .verifyComplete();

    CommitRequest.Builder builder =
        CommitRequest.newBuilder().setDatabase("projects/my-project/databases/(default)");

    builder.addWrites(Write.newBuilder().setUpdate(buildDocument("e1", null)).build());
    builder.addWrites(Write.newBuilder().setUpdate(buildDocument("e2", null)).build());

    verify(this.firestoreStub, times(1)).commit(eq(builder.build()), any());
  }

  @Test
  void deleteTest() {
    mockCommitMethod();

    StepVerifier.create(
            this.firestoreTemplate.delete(
                Flux.just(new TestEntity("e1", 100L), new TestEntity("e2", 200L))))
        .verifyComplete();

    CommitRequest.Builder builder =
        CommitRequest.newBuilder().setDatabase("projects/my-project/databases/(default)");

    builder.addWrites(Write.newBuilder().setDelete(parent + "/testEntities/e1").build());
    builder.addWrites(Write.newBuilder().setDelete(parent + "/testEntities/e2").build());

    verify(this.firestoreStub, times(1)).commit(eq(builder.build()), any());
  }

  @Test
  void deleteByIdTest() {
    mockCommitMethod();
    Flux<String> idPublisher = Flux.just("e1", "e2");
    StepVerifier.create(this.firestoreTemplate.deleteById(idPublisher, TestEntity.class))
        .verifyComplete();

    CommitRequest.Builder builder =
        CommitRequest.newBuilder().setDatabase("projects/my-project/databases/(default)");

    builder.addWrites(Write.newBuilder().setDelete(parent + "/testEntities/e1").build());
    builder.addWrites(Write.newBuilder().setDelete(parent + "/testEntities/e2").build());

    verify(this.firestoreStub).commit(eq(builder.build()), any());
  }

  private void mockCommitMethod() {
    doAnswer(
            invocation -> {
              StreamObserver<CommitResponse> streamObserver = invocation.getArgument(1);
              com.google.protobuf.Timestamp ts = Timestamp.ofTimeMicroseconds(123456789).toProto();
              CommitResponse response =
                  CommitResponse.newBuilder()
                      .addWriteResults(WriteResult.newBuilder().setUpdateTime(ts).build())
                      .addWriteResults(WriteResult.newBuilder().setUpdateTime(ts).build())
                      .build();
              streamObserver.onNext(response);
              streamObserver.onCompleted();
              return null;
            })
        .when(this.firestoreStub)
        .commit(any(), any());
  }

  @Test
  void findByIdTest() {
    doAnswer(
            invocation -> {
              StreamObserver<com.google.firestore.v1.Document> streamObserver =
                  invocation.getArgument(1);
              streamObserver.onNext(buildDocument("e1", 100L));

              streamObserver.onCompleted();
              return null;
            })
        .when(this.firestoreStub)
        .getDocument(any(), any());

    StepVerifier.create(this.firestoreTemplate.findById(Mono.just("e1"), TestEntity.class))
        .expectNext(new TestEntity("e1", 100L))
        .verifyComplete();

    GetDocumentRequest request =
        GetDocumentRequest.newBuilder().setName(parent + "/testEntities/" + "e1").build();

    verify(this.firestoreStub, times(1)).getDocument(eq(request), any());
  }

  @Test
  void findByIdErrorTest() {
    doAnswer(
            invocation -> {
              StreamObserver<com.google.firestore.v1.Document> streamObserver =
                  invocation.getArgument(1);
              streamObserver.onError(new RuntimeException("Firestore error"));
              return null;
            })
        .when(this.firestoreStub)
        .getDocument(any(), any());

    StepVerifier.create(this.firestoreTemplate.findById(Mono.just("e1"), TestEntity.class))
        .expectErrorMatches(
            e ->
                e instanceof FirestoreDataException
                    && e.getCause().getMessage().contains("Firestore error")
                    && e.getMessage().contains("Error while reading entries by id"))
        .verify();

    GetDocumentRequest request =
        GetDocumentRequest.newBuilder().setName(parent + "/testEntities/" + "e1").build();

    verify(this.firestoreStub, times(1)).getDocument(eq(request), any());
  }

  @Test
  void findByIdNotFoundTest() {
    doAnswer(
            invocation -> {
              StreamObserver<com.google.firestore.v1.Document> streamObserver =
                  invocation.getArgument(1);
              streamObserver.onError(new RuntimeException("NOT_FOUND: Document"));

              streamObserver.onCompleted();
              return null;
            })
        .when(this.firestoreStub)
        .getDocument(any(), any());

    StepVerifier.create(this.firestoreTemplate.findById(Mono.just("e1"), TestEntity.class))
        .verifyComplete();

    GetDocumentRequest request =
        GetDocumentRequest.newBuilder().setName(parent + "/testEntities/" + "e1").build();

    verify(this.firestoreStub, times(1)).getDocument(eq(request), any());
  }

  @Test
  void findAllByIdTest() {
    GetDocumentRequest request1 =
        GetDocumentRequest.newBuilder().setName(parent + "/testEntities/e1").build();

    GetDocumentRequest request2 =
        GetDocumentRequest.newBuilder().setName(parent + "/testEntities/e2").build();

    GetDocumentRequest request3 =
        GetDocumentRequest.newBuilder().setName(parent + "/testEntities/e3").build();

    doAnswer(
            invocation -> {
              StreamObserver<com.google.firestore.v1.Document> streamObserver =
                  invocation.getArgument(1);
              streamObserver.onNext(buildDocument("e1", 100L));

              streamObserver.onCompleted();
              return null;
            })
        .when(this.firestoreStub)
        .getDocument(eq(request1), any());

    doAnswer(
            invocation -> {
              StreamObserver<com.google.firestore.v1.Document> streamObserver =
                  invocation.getArgument(1);
              streamObserver.onNext(buildDocument("e2", 200L));

              streamObserver.onCompleted();
              return null;
            })
        .when(this.firestoreStub)
        .getDocument(eq(request2), any());

    doAnswer(
            invocation -> {
              StreamObserver<com.google.firestore.v1.Document> streamObserver =
                  invocation.getArgument(1);
              streamObserver.onError(new RuntimeException("NOT_FOUND: Document"));

              streamObserver.onCompleted();
              return null;
            })
        .when(this.firestoreStub)
        .getDocument(eq(request3), any());

    StepVerifier.create(this.firestoreTemplate.findAllById(Flux.just("e1", "e2"), TestEntity.class))
        .expectNext(new TestEntity("e1", 100L), new TestEntity("e2", 200L))
        .verifyComplete();

    verify(this.firestoreStub, times(1)).getDocument(eq(request1), any());
  }

  @Test
  void countTest() {
    mockRunQueryMethod();

    StepVerifier.create(this.firestoreTemplate.count(TestEntity.class))
        .expectNext(2L)
        .verifyComplete();

    StructuredQuery structuredQuery =
        StructuredQuery.newBuilder()
            .addFrom(
                StructuredQuery.CollectionSelector.newBuilder()
                    .setCollectionId("testEntities")
                    .build())
            .setSelect(
                StructuredQuery.Projection.newBuilder()
                    .addFields(
                        StructuredQuery.FieldReference.newBuilder()
                            .setFieldPath("__name__")
                            .build())
                    .build())
            .build();
    RunQueryRequest request =
        RunQueryRequest.newBuilder()
            .setParent(parent)
            .setStructuredQuery(structuredQuery)
            .build();

    verify(this.firestoreStub, times(1)).runQuery(eq(request), any());
    verify(this.firestoreStub, times(1)).runQuery(any(), any());
  }

  @Test
  void countWithQueryTest() {
    mockRunQueryMethod();

    StructuredQuery.Builder builder = StructuredQuery.newBuilder();
    addWhere(builder);

    StepVerifier.create(this.firestoreTemplate.count(TestEntity.class, builder))
        .expectNext(2L)
        .verifyComplete();

    StructuredQuery.Builder expectedBuilder =
        StructuredQuery.newBuilder()
            .addFrom(
                StructuredQuery.CollectionSelector.newBuilder()
                    .setCollectionId("testEntities")
                    .build())
            .setSelect(
                StructuredQuery.Projection.newBuilder()
                    .addFields(
                        StructuredQuery.FieldReference.newBuilder()
                            .setFieldPath("__name__")
                            .build())
                    .build());
    addWhere(expectedBuilder);

    RunQueryRequest request =
        RunQueryRequest.newBuilder()
            .setParent(parent)
            .setStructuredQuery(expectedBuilder)
            .build();

    verify(this.firestoreStub, times(1)).runQuery(eq(request), any());
    verify(this.firestoreStub, times(1)).runQuery(any(), any());
  }

  private void addWhere(StructuredQuery.Builder builder) {
    StructuredQuery.CompositeFilter.Builder compositeFilter =
        StructuredQuery.CompositeFilter.newBuilder();
    compositeFilter.setOp(StructuredQuery.CompositeFilter.Operator.AND);
    StructuredQuery.Filter.Builder filter = StructuredQuery.Filter.newBuilder();
    StructuredQuery.FieldReference fieldReference =
        StructuredQuery.FieldReference.newBuilder().setFieldPath("field_path").build();
    filter
        .getUnaryFilterBuilder()
        .setField(fieldReference)
        .setOp(StructuredQuery.UnaryFilter.Operator.IS_NULL);
    compositeFilter.addFilters(filter.build());
    builder.setWhere(
        StructuredQuery.Filter.newBuilder().setCompositeFilter(compositeFilter.build()));
  }

  @Test
  void existsByIdTest() {
    GetDocumentRequest request =
        GetDocumentRequest.newBuilder()
            .setName(parent + "/testEntities/" + "e1")
            .setMask(DocumentMask.newBuilder().addFieldPaths("__name__").build())
            .build();

    doAnswer(
            invocation -> {
              StreamObserver<com.google.firestore.v1.Document> streamObserver =
                  invocation.getArgument(1);
              streamObserver.onNext(buildDocument("e1", 100L));

              streamObserver.onCompleted();
              return null;
            })
        .when(this.firestoreStub)
        .getDocument(eq(request), any());

    StepVerifier.create(this.firestoreTemplate.existsById(Mono.just("e1"), TestEntity.class))
        .expectNext(Boolean.TRUE)
        .verifyComplete();

    verify(this.firestoreStub, times(1)).getDocument(eq(request), any());
    verify(this.firestoreStub, times(1)).getDocument(any(), any());
  }

  @Test
  void existsByIdNotFoundTest() {
    GetDocumentRequest request =
        GetDocumentRequest.newBuilder()
            .setName(parent + "/testEntities/" + "e1")
            .setMask(DocumentMask.newBuilder().addFieldPaths("__name__").build())
            .build();

    doAnswer(
            invocation -> {
              StreamObserver<com.google.firestore.v1.Document> streamObserver =
                  invocation.getArgument(1);
              streamObserver.onError(new RuntimeException("NOT_FOUND: Document"));

              streamObserver.onCompleted();
              return null;
            })
        .when(this.firestoreStub)
        .getDocument(eq(request), any());

    StepVerifier.create(this.firestoreTemplate.existsById(Mono.just("e1"), TestEntity.class))
        .expectNext(Boolean.FALSE)
        .verifyComplete();

    verify(this.firestoreStub, times(1)).getDocument(eq(request), any());
    verify(this.firestoreStub, times(1)).getDocument(any(), any());
  }

  @Test
  void withParentTest_entityReference() {
    doAnswer(
            invocation -> {
              StreamObserver<com.google.firestore.v1.Document> streamObserver =
                  invocation.getArgument(1);
              streamObserver.onNext(buildDocument("e1", 100L));
              streamObserver.onCompleted();
              return null;
            })
        .when(this.firestoreStub)
        .getDocument(any(), any());

    this.firestoreTemplate
        .withParent(new TestEntity("parent", 0L))
        .findById(Mono.just("child"), TestEntity.class)
        .block();

    GetDocumentRequest request =
        GetDocumentRequest.newBuilder()
            .setName(parent + "/testEntities/parent/testEntities/child")
            .build();

    verify(this.firestoreStub, times(1)).getDocument(eq(request), any());
  }

  @Test
  void withParentTest_idClassReference() {
    doAnswer(
            invocation -> {
              StreamObserver<com.google.firestore.v1.Document> streamObserver =
                  invocation.getArgument(1);
              streamObserver.onNext(buildDocument("e1", 100L));
              streamObserver.onCompleted();
              return null;
            })
        .when(this.firestoreStub)
        .getDocument(any(), any());

    this.firestoreTemplate
        .withParent("parent", TestEntity.class)
        .findById(Mono.just("child"), TestEntity.class)
        .block();

    GetDocumentRequest request =
        GetDocumentRequest.newBuilder()
            .setName(parent + "/testEntities/parent/testEntities/child")
            .build();

    verify(this.firestoreStub, times(1)).getDocument(eq(request), any());
  }

  private static Map<String, Value> createValuesMap(long value) {
    Map<String, Value> valuesMap = new HashMap<>();
    valuesMap.put("longField", Value.newBuilder().setIntegerValue(value).build());
    return valuesMap;
  }

  public static com.google.firestore.v1.Document buildDocument(String name, Long l) {
    Builder documentBuilder = com.google.firestore.v1.Document.newBuilder();
    if (name != null) {
      documentBuilder.setName(parent + "/testEntities/" + name);
    }
    if (l != null) {
      documentBuilder.putAllFields(createValuesMap(l));
    }
    return documentBuilder.build();
  }

  private void mockRunQueryMethod() {
    doAnswer(
            invocation -> {
              StreamObserver<RunQueryResponse> streamObserver = invocation.getArgument(1);
              streamObserver.onNext(
                  RunQueryResponse.newBuilder().setDocument(buildDocument("e1", 100L)).build());

              streamObserver.onNext(
                  RunQueryResponse.newBuilder().setDocument(buildDocument("e2", 200L)).build());

              streamObserver.onCompleted();
              return null;
            })
        .when(this.firestoreStub)
        .runQuery(any(), any());
  }

  @Document(collectionName = "testEntities")
  public static class TestEntity {
    @DocumentId String idField;

    Long longField;

    @UpdateTime Timestamp updateTimestamp;

    TestEntity() {}

    public TestEntity(String idField, Long longField) {
      this.idField = idField;
      this.longField = longField;
    }

    public String getIdField() {
      return this.idField;
    }

    public void setIdField(String idField) {
      this.idField = idField;
    }

    public Long getLongField() {
      return this.longField;
    }

    public void setLongField(Long longField) {
      this.longField = longField;
    }

    public Timestamp getUpdateTimestamp() {
      return updateTimestamp;
    }

    public void setUpdateTimestamp(Timestamp updateTimestamp) {
      this.updateTimestamp = updateTimestamp;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      TestEntity that = (TestEntity) o;
      return Objects.equals(getIdField(), that.getIdField())
          && Objects.equals(getLongField(), that.getLongField());
    }

    @Override
    public int hashCode() {
      return Objects.hash(getIdField(), getLongField());
    }
  }

  @Document(collectionName = "testEntities")
  static class TestEntityUpdateTimeVersion {
    @DocumentId public String id;

    @UpdateTime(version = true)
    public Timestamp updateTime;

    TestEntityUpdateTimeVersion(String id) {
      this.id = id;
    }

    TestEntityUpdateTimeVersion(String id, Timestamp updateTime) {
      this.id = id;
      this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof TestEntityUpdateTimeVersion that)) {
        return false;
      }

      if (!Objects.equals(id, that.id)) {
        return false;
      }
      return Objects.equals(updateTime, that.updateTime);
    }

    @Override
    public int hashCode() {
      int result = id != null ? id.hashCode() : 0;
      result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
      return result;
    }
  }

  @Document(collectionName = "testEntities")
  static class TestEntityUpdateTime {
    @DocumentId public String id;

    @UpdateTime public Timestamp updateTime;

    TestEntityUpdateTime(String id) {
      this.id = id;
    }

    TestEntityUpdateTime(String id, Timestamp updateTime) {
      this.id = id;
      this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (!(o instanceof TestEntityUpdateTime that)) {
        return false;
      }

      if (!Objects.equals(id, that.id)) {
        return false;
      }
      return Objects.equals(updateTime, that.updateTime);
    }

    @Override
    public int hashCode() {
      int result = id != null ? id.hashCode() : 0;
      result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
      return result;
    }

    @Override
    public String toString() {
      return "TestEntityUpdateTime{" + "id='" + id + '\'' + ", updateTime=" + updateTime + '}';
    }
  }
}
