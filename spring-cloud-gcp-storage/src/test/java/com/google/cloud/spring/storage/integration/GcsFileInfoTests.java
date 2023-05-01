package com.google.cloud.spring.storage.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.cloud.storage.BlobInfo;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

@TestInstance(Lifecycle.PER_CLASS)
class GcsFileInfoTests {

  private final BlobInfo gcsFile = mock(BlobInfo.class);

  private final GcsFileInfo gcsFileInfo = new GcsFileInfo(gcsFile);

  private final OffsetDateTime now = OffsetDateTime.now();

  private final Random random = new Random();

  private boolean randomBoolean;

  private long randomSize;

  @BeforeAll
  void init() {
    randomBoolean = random.nextBoolean();
    randomSize = random.nextLong();
    when(gcsFile.isDirectory()).thenReturn(randomBoolean);
    when(gcsFile.getSize()).thenReturn(randomSize);
    when(gcsFile.getUpdateTimeOffsetDateTime()).thenReturn(now);
    when(gcsFile.getName()).thenReturn("fake-name");
  }

  @Test
  void isDirectoryTest() {
    assertThat(gcsFileInfo.isDirectory()).isEqualTo(randomBoolean);
  }

  @Test
  void isLinkTest() {
    assertThat(gcsFileInfo.isLink()).isFalse();
  }

  @Test
  void getSizeTest() {
    assertThat(gcsFileInfo.getSize()).isEqualTo(randomSize);
  }

  @Test
  void getModifiedTest() {
    assertThat(gcsFileInfo.getModified()).isEqualTo(now.toInstant().toEpochMilli());
  }

  @Test
  void getFileNameTest() {
    assertThat(gcsFileInfo.getFilename()).isEqualTo("fake-name");
  }

  @Test
  void getPermissionsTest() {
    assertThatThrownBy(gcsFileInfo::getPermissions)
        .isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void getFileInfoTest() {
    assertThat(gcsFileInfo.getFileInfo()).isEqualTo(gcsFile);
  }

  @Test
  void toStringTest() {
    assertThat(gcsFileInfo).hasToString(
        "FileInfo [isDirectory="
            + randomBoolean
            + ", isLink="
            + false
            + ", Size="
            + randomSize
            + ", ModifiedTime="
            + new Date(now.toInstant().toEpochMilli())
            + ", Filename="
            + "fake-name"
            + ", RemoteDirectory="
            + null
            + "]"
    );
  }
}
