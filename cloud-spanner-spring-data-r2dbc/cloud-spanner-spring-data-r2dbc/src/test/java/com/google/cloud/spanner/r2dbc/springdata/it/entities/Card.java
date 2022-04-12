/*
 * Copyright 2022-2022 Google LLC
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

package com.google.cloud.spanner.r2dbc.springdata.it.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.data.relational.core.mapping.Column;

/** Example entity. */
public class Card {

  @Column("id")
  private long id;

  @Column("expiry_year")
  private int expiryYear;

  @Column("expiry_month")
  private int expiryMonth;

  @Column("issue_date")
  private LocalDate issueDate;

  @Column("requested_at")
  private LocalDateTime requestedAt;

  /** Constructor. */
  public Card(long id, int expiryYear, int expiryMonth, LocalDate issueDate,
              LocalDateTime requestedAt) {
    this.id = id;
    this.expiryYear = expiryYear;
    this.expiryMonth = expiryMonth;
    this.issueDate = issueDate;
    this.requestedAt = requestedAt;
  }

  public long getId() {
    return this.id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public int getExpiryYear() {
    return this.expiryYear;
  }

  public void setExpiryYear(int expiryYear) {
    this.expiryYear = expiryYear;
  }

  public int getExpiryMonth() {
    return this.expiryMonth;
  }

  public void setExpiryMonth(int expiryMonth) {
    this.expiryMonth = expiryMonth;
  }

  public LocalDate getIssueDate() {
    return this.issueDate;
  }

  public void setIssueDate(LocalDate issueDate) {
    this.issueDate = issueDate;
  }

  public LocalDateTime getRequestedAt() {
    return this.requestedAt;
  }

  public void setRequestedAt(LocalDateTime requestedAt) {
    this.requestedAt = requestedAt;
  }
}
