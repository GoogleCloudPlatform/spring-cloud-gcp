/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/** Book entity. */
@Table
public class Book implements Persistable<String> {

  @Id
  @Column("ID")
  private String id;

  @Column("TITLE")
  private String title;

  @Column("EXTRADETAILS")
  private Map<String, String> extraDetails;

  @Column("REVIEWS")
  private Review review;

  @Column("CATEGORIES")
  private List<String> categories;

  @Column("COUNT")
  private int count;

  @Transient
  private boolean isNew;

  public Book(String title, Map<String, String> extraDetails, Review review) {
    this.id = UUID.randomUUID().toString();
    this.title = title;
    this.extraDetails = extraDetails;
    this.review = review;
    this.count = 0;
    this.isNew = true;
  }

  public String getId() {
    return id;
  }

  @Override
  public boolean isNew() {
    return this.isNew;
  }

  public String getTitle() {
    return this.title;
  }

  public Map<String, String> getExtraDetails() {
    return extraDetails;
  }

  public Review getReview() {
    return review;
  }

  public List<String> getCategories() {
    return categories;
  }

  public void setCategories(List<String> categories) {
    this.categories = categories;
  }

  public int getCount() {
    return count;
  }

  public void incrementCount() {
    this.count++;
    this.isNew = false;
  }

  @Override
  public String toString() {
    return "Book{"
        + "id='"
        + id
        + '\''
        + ", title='"
        + title
        + '\''
        + ", extraDetails="
        + (extraDetails == null ? "" : extraDetails.toString())
        + ", categories="
        + (categories == null ? "" : categories)
        + '}';
  }
}
