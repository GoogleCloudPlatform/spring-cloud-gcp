/*
 * Copyright 2021 Google LLC
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

import com.google.common.base.Objects;

public class Review {
    String reviewerId;
    String reviewerContent;

    public String getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(String reviewerId) {
        this.reviewerId = reviewerId;
    }

    public String getReviewerContent() {
        return reviewerContent;
    }

    public void setReviewerContent(String reviewerContent) {
        this.reviewerContent = reviewerContent;
    }

    public Review() {
    }

    public Review(String reviewerId, String reviewerContent) {
        this.reviewerId = reviewerId;
        this.reviewerContent = reviewerContent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Review review = (Review) o;
        return Objects.equal(reviewerId, review.reviewerId) && Objects.equal(reviewerContent, review.reviewerContent);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(reviewerId, reviewerContent);
    }
}
