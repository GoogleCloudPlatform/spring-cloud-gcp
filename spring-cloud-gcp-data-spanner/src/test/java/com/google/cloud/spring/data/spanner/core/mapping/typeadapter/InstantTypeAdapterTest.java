package com.google.cloud.spring.data.spanner.core.mapping.typeadapter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.Instant;
import org.junit.Test;

public class InstantTypeAdapterTest {

  @Test
  public void writeInstantTest() throws IOException {
    InstantTypeAdapter instantTypeAdapter = new InstantTypeAdapter();
    StringWriter stringWriter = new StringWriter();
    JsonWriter jsonWriter = new JsonWriter(stringWriter);
    Instant instant = Instant.ofEpochSecond(0);

    instantTypeAdapter.write(jsonWriter, instant);

    assertThat(stringWriter.toString()).isEqualTo("\"1970-01-01T00:00:00Z\"");
  }

  @Test
  public void writeNullInstantTest() throws IOException {
    InstantTypeAdapter instantTypeAdapter = new InstantTypeAdapter();
    StringWriter stringWriter = new StringWriter();
    JsonWriter jsonWriter = new JsonWriter(stringWriter);
    Instant instant = null;

    instantTypeAdapter.write(jsonWriter, instant);

    assertThat(stringWriter.toString()).isEqualTo("null");
  }

  @Test
  public void readInstantTest() throws IOException {
    InstantTypeAdapter instantTypeAdapter = new InstantTypeAdapter();
    String instantString = "\"1970-01-01T00:00:00Z\"";
    Instant instant = Instant.ofEpochSecond(0);
    StringReader stringReader = new StringReader(instantString);
    Instant readInstant = instantTypeAdapter.read(new JsonReader(stringReader));

    assertThat(readInstant).isEqualTo(instant);
  }

  @Test
  public void readNullInstantTest() throws IOException {
    InstantTypeAdapter instantTypeAdapter = new InstantTypeAdapter();
    String instantString = "null";
    StringReader stringReader = new StringReader(instantString);
    Instant readInstant = instantTypeAdapter.read(new JsonReader(stringReader));

    assertThat(readInstant).isNull();
  }
}
