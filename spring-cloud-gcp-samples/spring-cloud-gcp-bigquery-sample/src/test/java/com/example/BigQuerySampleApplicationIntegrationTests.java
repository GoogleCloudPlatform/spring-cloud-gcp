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

package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableId;
import com.google.cloud.bigquery.TableResult;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;

/** Tests the {@link BigQuerySampleApplication} POST endpoints. */
// In order to enable the tests, please use '-Dit.bigquery=true' .
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = BigQuerySampleApplication.class,
    properties = "spring.cloud.gcp.bigquery.datasetName=test_dataset")
@EnabledIfSystemProperty(named = "it.bigquery", matches = "true")
@ImportRuntimeHints({TestResourcesRuntimeHints.class})
class BigQuerySampleApplicationIntegrationTests {

  private static final String DATASET_NAME = "test_dataset";

  private static final String TABLE_NAME_PREFIX = "bigquery_sample_test_table";

  private static String TABLE_NAME;

  @Autowired BigQuery bigQuery;

  @Autowired TestRestTemplate restTemplate;

  @Value("classpath:test.csv")
  Resource csvFile;

  @Value("classpath:test.json")
  Resource jsonFile;

  @AfterEach
  void cleanupTestEnvironment() {
    // Clear the previous dataset before beginning the test.
    this.bigQuery.delete(TableId.of(DATASET_NAME, TABLE_NAME));
  }

  @BeforeEach
  void setTableName() {
    // adds a 5 char pseudo rand number suffix to make the table name unique before every run
    TABLE_NAME = TABLE_NAME_PREFIX + getRandSuffix();
  }

  // returns a 5 char pseudo rand number suffix
  private String getRandSuffix() {
    return UUID.randomUUID().toString().substring(0, 5);
  }

  @Test
  void testJsonTextUpload() throws InterruptedException {
    String jsonTxt =
        """
            {"CompanyName":"TALES","Description":"mark","SerialNumber":97,"Leave":0,"EmpName":"Mark"}
            {"CompanyName":"1Q84","Description":"ark","SerialNumber":978,"Leave":0,"EmpName":"HARUKI"}
            {"CompanyName":"MY","Description":"M","SerialNumber":9780,"Leave":0,"EmpName":"Mark"}""";

    LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    map.add("jsonRows", jsonTxt);
    map.add("tableName", TABLE_NAME);
    map.add("createTable", "createTable");

    HttpHeaders headers = new HttpHeaders();
    HttpEntity<LinkedMultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
    ResponseEntity<String> response =
        this.restTemplate.postForEntity("/uploadJsonText", request, String.class);
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    QueryJobConfiguration queryJobConfiguration =
        QueryJobConfiguration.newBuilder(
                "SELECT * FROM " + DATASET_NAME + "." + TABLE_NAME + " order by SerialNumber desc")
            .build();

    TableResult queryResult = this.bigQuery.query(queryJobConfiguration);
    assertThat(queryResult.getTotalRows()).isEqualTo(3);
    FieldValueList row = queryResult.getValues().iterator().next(); // match the first record
    assertThat(row.get("SerialNumber").getLongValue()).isEqualTo(9780L);
    assertThat(row.get("EmpName").getStringValue()).isEqualTo("Mark");
  }

  @Test
  void testJsonFileUpload() throws InterruptedException {
    LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    map.add("file", jsonFile);
    map.add("tableName", TABLE_NAME);
    map.add("createTable", "createTable");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    HttpEntity<LinkedMultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
    ResponseEntity<String> response =
        this.restTemplate.postForEntity("/uploadJsonFile", request, String.class);
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

    QueryJobConfiguration queryJobConfiguration =
        QueryJobConfiguration.newBuilder(
                "SELECT * FROM " + DATASET_NAME + "." + TABLE_NAME + " order by SerialNumber desc")
            .build();

    TableResult queryResult = this.bigQuery.query(queryJobConfiguration);
    assertThat(queryResult.getTotalRows()).isEqualTo(3);
    FieldValueList row = queryResult.getValues().iterator().next(); // match the first record
    assertThat(row.get("SerialNumber").getLongValue()).isEqualTo(9780L);
    assertThat(row.get("EmpName").getStringValue()).isEqualTo("Mark");
  }

  @Test
  void testFileUpload() throws InterruptedException {
    LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    map.add("file", csvFile);
    map.add("tableName", TABLE_NAME);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    HttpEntity<LinkedMultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
    ResponseEntity<String> response =
        this.restTemplate.postForEntity("/uploadFile", request, String.class);
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

    QueryJobConfiguration queryJobConfiguration =
        QueryJobConfiguration.newBuilder("SELECT * FROM " + DATASET_NAME + "." + TABLE_NAME)
            .build();

    TableResult queryResult = this.bigQuery.query(queryJobConfiguration);
    assertThat(queryResult.getTotalRows()).isEqualTo(3);

    List<String> names =
        StreamSupport.stream(queryResult.getValues().spliterator(), false)
            .map(valueList -> valueList.get(0).getStringValue())
            .collect(Collectors.toList());
    assertThat(names).containsExactlyInAnyOrder("Nathaniel", "Diaz", "Johnson");
  }

  @Test
  void testCsvDataUpload() throws InterruptedException {
    LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
    map.add("csvText", "name,age,location\nBob,24,Wyoming");
    map.add("tableName", TABLE_NAME);

    HttpHeaders headers = new HttpHeaders();
    HttpEntity<LinkedMultiValueMap<String, Object>> request = new HttpEntity<>(map, headers);
    ResponseEntity<String> response =
        this.restTemplate.postForEntity("/uploadCsvText", request, String.class);
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();

    QueryJobConfiguration queryJobConfiguration =
        QueryJobConfiguration.newBuilder("SELECT * FROM " + DATASET_NAME + "." + TABLE_NAME)
            .build();

    TableResult queryResult = this.bigQuery.query(queryJobConfiguration);
    assertThat(queryResult.getTotalRows()).isEqualTo(1);

    FieldValueList row = queryResult.getValues().iterator().next();
    assertThat(row.get(0).getStringValue()).isEqualTo("Bob");
    assertThat(row.get(1).getLongValue()).isEqualTo(24);
    assertThat(row.get(2).getStringValue()).isEqualTo("Wyoming");
  }
}
