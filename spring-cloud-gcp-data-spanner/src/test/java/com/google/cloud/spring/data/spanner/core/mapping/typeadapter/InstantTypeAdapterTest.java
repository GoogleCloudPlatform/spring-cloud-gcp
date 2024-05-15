package com.google.cloud.spring.data.spanner.core.mapping.typeadapter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.Instant;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

public class InstantTypeAdapterTest {

  @ParameterizedTest
  @ValueSource(ints = {Integer.MIN_VALUE, -1000, 0, 1000, 78796800, 1635642000, Integer.MAX_VALUE})
  public void writeInstantTest(int epochSecond) throws IOException {
    InstantTypeAdapter instantTypeAdapter = new InstantTypeAdapter();
    StringWriter stringWriter = new StringWriter();
    JsonWriter jsonWriter = new JsonWriter(stringWriter);
    Instant instant = Instant.ofEpochSecond(epochSecond);

    instantTypeAdapter.write(jsonWriter, instant);

    assertThat(stringWriter.toString()).isEqualTo("\"" + instant.toString() + "\"");
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "1901-12-13T20:45:52Z",
        "1969-12-31T23:43:20Z",
        "1970-01-01T00:00:00Z",
        "1970-01-01T00:16:40Z",
        "1972-07-01T00:00:00Z",
        "2021-10-31T01:00:00Z",
        "2038-01-19T03:14:07Z"
      })
  public void readInstantTest(String instantString) throws IOException {
    InstantTypeAdapter instantTypeAdapter = new InstantTypeAdapter();
    Instant instant = Instant.parse(instantString);
    StringReader stringReader = new StringReader("\"" + instantString + "\"");
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

  @Test
  public void writeNullInstantTest() throws IOException {
    InstantTypeAdapter instantTypeAdapter = new InstantTypeAdapter();
    StringWriter stringWriter = new StringWriter();
    JsonWriter jsonWriter = new JsonWriter(stringWriter);
    Instant instant = null;

    instantTypeAdapter.write(jsonWriter, instant);

    assertThat(stringWriter.toString()).isEqualTo("null");
  }
}
