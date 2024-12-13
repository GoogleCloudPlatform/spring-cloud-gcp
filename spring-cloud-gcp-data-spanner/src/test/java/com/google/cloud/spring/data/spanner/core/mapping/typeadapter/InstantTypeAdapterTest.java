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
  InstantTypeAdapter instantTypeAdapter = new InstantTypeAdapter();
  StringWriter stringWriter = new StringWriter();
  JsonWriter jsonWriter = new JsonWriter(stringWriter);

  @Test
  public void writeInstantTest_epochSecond0() throws IOException {
    Instant instant = Instant.ofEpochSecond(0);

    instantTypeAdapter.write(jsonWriter, instant);

    assertThat(stringWriter.toString()).isEqualTo("\"1970-01-01T00:00:00Z\"");
  }

  @Test
  public void writeInstantTest_epochSecond817() throws IOException {
    Instant instant = Instant.ofEpochSecond(817);

    instantTypeAdapter.write(jsonWriter, instant);

    assertThat(stringWriter.toString()).isEqualTo("\"1970-01-01T00:13:37Z\"");
  }

  @Test
  public void writeNullInstantTest() throws IOException {
    Instant instant = null;

    instantTypeAdapter.write(jsonWriter, instant);

    assertThat(stringWriter.toString()).isEqualTo("null");
  }

  @Test
  public void readInstantTest_epochSecond0() throws IOException {
    StringReader stringReader = new StringReader("\"1970-01-01T00:00:00Z\"");

    Instant readInstant = instantTypeAdapter.read(new JsonReader(stringReader));

    assertThat(readInstant).isEqualTo(Instant.ofEpochSecond(0));
  }

  @Test
  public void readInstantTest_epochSecond42() throws IOException {
    StringReader stringReader = new StringReader("\"1970-01-01T00:00:42Z\"");

    Instant readInstant = instantTypeAdapter.read(new JsonReader(stringReader));

    assertThat(readInstant).isEqualTo(Instant.ofEpochSecond(42));
  }

  @Test
  public void readNullInstantTest() throws IOException {
    StringReader stringReader = new StringReader("null");

    Instant readInstant = instantTypeAdapter.read(new JsonReader(stringReader));

    assertThat(readInstant).isNull();
  }
}
