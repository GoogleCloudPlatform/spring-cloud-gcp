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

import com.example.BigQuerySampleConfiguration.BigQueryFileGateway;
import com.google.cloud.bigquery.Field;
import com.google.cloud.bigquery.FormatOptions;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.Schema;
import com.google.cloud.bigquery.StandardSQLTypeName;
import com.google.cloud.spring.bigquery.core.BigQueryTemplate;
import com.google.cloud.spring.bigquery.core.WriteApiResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

/** Provides REST endpoint allowing you to load data files to BigQuery using Spring Integration. */
@Controller
public class WebController {

  private final BigQueryFileGateway bigQueryFileGateway;

  private final BigQueryTemplate bigQueryTemplate;

  private static final String DATASET_NAME = "datasetName";

  @Value("${spring.cloud.gcp.bigquery.datasetName}")
  private String datasetName;

  public WebController(BigQueryFileGateway bigQueryFileGateway,
      BigQueryTemplate bigQueryTemplate) {
    this.bigQueryFileGateway = bigQueryFileGateway;
    this.bigQueryTemplate = bigQueryTemplate;
  }

  @GetMapping("/")
  public ModelAndView renderIndex(ModelMap map) {
    map.put(DATASET_NAME, this.datasetName);
    return new ModelAndView("index.html", map);
  }

  @GetMapping("/write-api-json-upload")
  public ModelAndView renderUploadJson(ModelMap map) {
    map.put(DATASET_NAME, this.datasetName);
    return new ModelAndView("upload-json.html", map);
  }

  /**
   * Handles a file upload using {@link BigQueryTemplate}.
   *
   * @param file the JSON file to upload to BigQuery
   * @param tableName name of the table to load data into
   * @return ModelAndView of the response the send back to users
   * @throws IOException if the file is unable to be loaded.
   */
  @PostMapping("/uploadJsonFile")
  public ModelAndView handleJsonFileUpload(
      @RequestParam("file") MultipartFile file,
      @RequestParam("tableName") String tableName,
      @RequestParam(name = "createTable", required = false) String createDefaultTable)
      throws IOException {
    CompletableFuture<WriteApiResponse> writeApiRes;
    if (createDefaultTable != null
        && createDefaultTable.equals("createTable")) { // create the default table
      writeApiRes =
          this.bigQueryTemplate.writeJsonStream(
              tableName, file.getInputStream(), getDefaultSchema());
    } else { // we are expecting the table to be already existing
      writeApiRes = this.bigQueryTemplate.writeJsonStream(tableName, file.getInputStream());
    }
    return getWriteApiResponse(writeApiRes, tableName);
  }

  private Schema getDefaultSchema() {
    return Schema.of(
        Field.of("CompanyName", StandardSQLTypeName.STRING),
        Field.of("Description", StandardSQLTypeName.STRING),
        Field.of("SerialNumber", StandardSQLTypeName.NUMERIC),
        Field.of("Leave", StandardSQLTypeName.NUMERIC),
        Field.of("EmpName", StandardSQLTypeName.STRING));
  }

  /**
   * Handles JSON data upload using using {@link BigQueryTemplate}.
   *
   * @param jsonRows the String JSON data to upload to BigQuery
   * @param tableName name of the table to load data into
   * @return ModelAndView of the response the send back to users
   */
  @PostMapping("/uploadJsonText")
  public ModelAndView handleJsonTextUpload(
      @RequestParam("jsonRows") String jsonRows,
      @RequestParam("tableName") String tableName,
      @RequestParam(name = "createTable", required = false) String createDefaultTable) {
    CompletableFuture<WriteApiResponse> writeApiRes;
    if (createDefaultTable != null
        && createDefaultTable.equals("createTable")) { // create the default table

      writeApiRes =
          this.bigQueryTemplate.writeJsonStream(
              tableName, new ByteArrayInputStream(jsonRows.getBytes()), getDefaultSchema());
    } else { // we are expecting the table to be already existing
      writeApiRes =
          this.bigQueryTemplate.writeJsonStream(
              tableName, new ByteArrayInputStream(jsonRows.getBytes()));
    }
    return getWriteApiResponse(writeApiRes, tableName);
  }

  private ModelAndView getWriteApiResponse(
      CompletableFuture<WriteApiResponse> writeApiFuture, String tableName) {
    String message = null;
    try {
      WriteApiResponse apiResponse = writeApiFuture.get();
      if (apiResponse.isSuccessful()) {
        message = "Successfully loaded data to " + tableName;
      } else if (apiResponse.getErrors() != null && !apiResponse.getErrors().isEmpty()) {
        message =
            String.format(
                "Error occurred while loading the file, printing first error %s. Use WriteApiResponse.getErrors() to get the complete list of errors",
                apiResponse.getErrors().get(0).getErrorMessage());
      }

    } catch (Exception e) {
      e.printStackTrace();
      message = "Error: " + e.getMessage();
    }
    return new ModelAndView("upload-json.html")
        .addObject(DATASET_NAME, this.datasetName)
        .addObject("message", message);
  }

  /**
   * Handles a file upload using {@link BigQueryTemplate}.
   *
   * @param file the CSV file to upload to BigQuery
   * @param tableName name of the table to load data into
   * @return ModelAndView of the response to send back to users
   * @throws IOException if the file is unable to be loaded.
   */
  @PostMapping("/uploadFile")
  public ModelAndView handleFileUpload(
      @RequestParam("file") MultipartFile file, @RequestParam("tableName") String tableName)
      throws IOException {

    CompletableFuture<Job> loadJob =
        this.bigQueryTemplate.writeDataToTable(
            tableName, file.getInputStream(), FormatOptions.csv());

    return getResponse(loadJob, tableName);
  }

  /**
   * Handles CSV data upload using Spring Integration {@link BigQueryFileGateway}.
   *
   * @param csvData the String CSV data to upload to BigQuery
   * @param tableName name of the table to load data into
   * @return ModelAndView of the response the send back to users
   */
  @PostMapping("/uploadCsvText")
  public ModelAndView handleCsvTextUpload(
      @RequestParam("csvText") String csvData, @RequestParam("tableName") String tableName) {

    CompletableFuture<Job> loadJob =
        this.bigQueryFileGateway.writeToBigQueryTable(csvData.getBytes(), tableName);

    return getResponse(loadJob, tableName);
  }

  private ModelAndView getResponse(CompletableFuture<Job> loadJob, String tableName) {
    String message;
    try {
      Job job = loadJob.get();
      message = "Successfully loaded data file to " + tableName;
    } catch (Exception e) {
      e.printStackTrace();
      message = "Error: " + e.getMessage();
    }

    return new ModelAndView("index")
        .addObject(DATASET_NAME, this.datasetName)
        .addObject("message", message);
  }
}
