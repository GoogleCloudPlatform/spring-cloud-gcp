/*
 * Copyright 2019-2020 Google LLC
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

package com.google.cloud.spanner.r2dbc.it;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Convenience class to represent a book in integration tests. This is here for easy equals().
 */
public class Book {

  private final String id;

  private final String title;

  private final String author;

  private final String synopsis;

  private final String[] editions;

  private final List<String> awards;

  private final Boolean fiction;

  private final LocalDate published;

  private final Double wordsPerSentence;

  private final Integer category;

  private final BigDecimal price;

  /**
   * Constructor.
   *
   * @param id id
   * @param title title
   * @param author author
   * @param synopsis text
   * @param editions edition array
   * @awards awards awards list
   * @param fiction boolean
   * @param published local date
   * @param wordsPerSentence double
   * @param category integer
   */
  public Book(String id, String title, String author, String synopsis,
      String[] editions, List<String> awards, Boolean fiction, LocalDate published,
      Double wordsPerSentence, Integer category, BigDecimal price) {
    this.id = id;
    this.title = title;
    this.author = author;
    this.synopsis = synopsis;
    this.editions = editions;
    this.awards = awards;
    this.fiction = fiction;
    this.published = published;
    this.wordsPerSentence = wordsPerSentence;
    this.category = category;
    this.price = price;
  }

  public String getId() {
    return this.id;
  }

  public String getTitle() {
    return this.title;
  }

  public String getAuthor() {
    return this.author;
  }

  public String getSynopsis() {
    return this.synopsis;
  }

  public String[] getEditions() {
    return this.editions;
  }

  public List<String> getAwards() {
    return this.awards;
  }

  public Boolean getFiction() {
    return this.fiction;
  }

  public LocalDate getPublished() {
    return this.published;
  }

  public Double getWordsPerSentence() {
    return this.wordsPerSentence;
  }

  public Integer getCategory() {
    return this.category;
  }

  public BigDecimal getPrice() {
    return this.price;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Book that = (Book) o;
    return Objects.equals(this.id, that.id)
        && Objects.equals(this.title, that.title)
        && Objects.equals(this.author, that.author)
        && Objects.equals(this.synopsis, that.synopsis)
        && Arrays.equals(this.editions, that.editions)
        && Objects.equals(this.fiction, that.fiction)
        && Objects.equals(this.published, that.published)
        && Objects.equals(this.wordsPerSentence, that.wordsPerSentence)
        && Objects.equals(this.category, that.category);
  }
}
