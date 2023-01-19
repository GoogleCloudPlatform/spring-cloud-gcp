/*
 * Copyright 2017-2020 the original author or authors.
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

package com.google.cloud.spring.storage.integration.filters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.cloud.storage.BlobInfo;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;

/** Tests for GcsAcceptModifiedAfterFileListFilter. */
class GcsAcceptModifiedAfterFileListFilterTest {

  @Test
  void addDiscardCallback() {
    GcsAcceptModifiedAfterFileListFilter filter = new GcsAcceptModifiedAfterFileListFilter();

    AtomicBoolean callbackTriggered = new AtomicBoolean(false);
    filter.addDiscardCallback(blobInfo -> callbackTriggered.set(true));

    BlobInfo blobInfo = mock(BlobInfo.class);
    when(blobInfo.getUpdateTimeOffsetDateTime()).thenReturn(OffsetDateTime.now().minusDays(1L));

    filter.accept(blobInfo);

    assertThat(callbackTriggered.get()).isTrue();
  }

  @Test
  void filterFiles() {

    OffsetDateTime now = OffsetDateTime.now();

    BlobInfo oldBlob = mock(BlobInfo.class);
    when(oldBlob.getUpdateTimeOffsetDateTime()).thenReturn(now.minusMinutes(1L));

    BlobInfo currentBlob = mock(BlobInfo.class);
    when(currentBlob.getUpdateTimeOffsetDateTime()).thenReturn(now);

    BlobInfo newBlob = mock(BlobInfo.class);
    when(newBlob.getUpdateTimeOffsetDateTime()).thenReturn(now.plusMinutes(1L));

    ArrayList<BlobInfo> expected = new ArrayList<>();
    expected.add(currentBlob);
    expected.add(newBlob);

    assertThat(
            new GcsAcceptModifiedAfterFileListFilter(now.toInstant())
                .filterFiles(new BlobInfo[] {oldBlob, currentBlob, newBlob}))
        .isEqualTo(expected);
  }

  @Test
  void supportsSingleFileFiltering() {
    assertThat(new GcsAcceptModifiedAfterFileListFilter().supportsSingleFileFiltering()).isTrue();
  }
}
