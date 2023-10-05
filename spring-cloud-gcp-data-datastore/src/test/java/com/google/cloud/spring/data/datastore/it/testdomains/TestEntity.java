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

package com.google.cloud.spring.data.datastore.it.testdomains;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Blob;
import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import java.util.Objects;
import org.springframework.data.annotation.Id;

/** A test entity for Datastore integration tests. */
@Entity(name = "test_entities_#{\"ci\"}")
public class TestEntity {

  @Id private Long id;

  private String color;

  private Long size;

  private Shape shape;

  private Blob blobField;

  private Timestamp datetime;

  EmbeddedEntity embeddedEntity;

  TestEntity() {}

  public TestEntity(Long id, String color, Long size, Shape shape, Blob blobField) {
    this.id = id;
    this.color = color;
    this.size = size;
    this.shape = shape;
    this.blobField = blobField;
  }

  public TestEntity(
      Long id,
      String color,
      Long size,
      Shape shape,
      Blob blobField,
      EmbeddedEntity embeddedEntity) {
    this.id = id;
    this.color = color;
    this.size = size;
    this.shape = shape;
    this.blobField = blobField;
    this.embeddedEntity = embeddedEntity;
  }

  public TestEntity(Long id, String color, Long size, Timestamp datetime) {
    this.id = id;
    this.color = color;
    this.size = size;
    this.datetime = datetime;
  }

  public Shape getShape() {
    return this.shape;
  }

  void setShape(Shape shape) {
    this.shape = shape;
  }

  public Long getId() {
    return this.id;
  }

  void setId(Long id) {
    this.id = id;
  }

  public Blob getBlobField() {
    return this.blobField;
  }

  public void setBlobField(Blob blobField) {
    this.blobField = blobField;
  }

  public String getColor() {
    return this.color;
  }

  void setColor(String color) {
    this.color = color;
  }

  Long getSize() {
    return this.size;
  }

  void setSize(Long size) {
    this.size = size;
  }

  Timestamp getDatetime() {
    return datetime;
  }

  void setDatetime(Timestamp datetime) {
    this.datetime = datetime;
  }

  /** An enum that tests conversion and storage. */
  public enum Shape {
    CIRCLE,
    SQUARE;
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
    return Objects.equals(getId(), that.getId())
        && Objects.equals(getColor(), that.getColor())
        && Objects.equals(getSize(), that.getSize())
        && getShape() == that.getShape()
        && Objects.equals(getBlobField(), that.getBlobField());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getColor(), getSize(), getShape(), getBlobField());
  }

  @Override
  public String toString() {
    return "TestEntity{"
        + "id="
        + id
        + ", color='"
        + color
        + '\''
        + ", size="
        + size
        + ", shape="
        + shape
        + ", blobField="
        + blobField
        + ", embeddedEntity="
        + embeddedEntity
        + ", datetime="
        + datetime
        + '}';
  }
}
