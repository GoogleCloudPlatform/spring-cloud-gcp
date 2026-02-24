package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.spring.parametermanager.ParameterManagerTemplate;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@EnabledIfSystemProperty(named = "it.parametermanager", matches = "true")
@ExtendWith(SpringExtension.class)
@AutoConfigureTestRestTemplate
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = ParameterManagerApplication.class)
public class ParameterManagerRegionalSampleTemplateIntegrationTests {

  @Autowired private ParameterManagerTemplate parameterManagerTemplate;

  @Autowired private TestRestTemplate testRestTemplate;

  private String parameterName;
  private String versionName;

  @BeforeEach
  void createParameter() {
    this.parameterName = String.format("sample-parameter-%s", UUID.randomUUID());
    this.versionName = String.format("sample-version-%s", UUID.randomUUID());
    parameterManagerTemplate.createParameter(
        "us-central1", this.parameterName, this.versionName, "{'message': 'Hello World'}");
  }

  @AfterEach
  void deleteParameter() {
    if (parameterManagerTemplate.parameterVersionExists("us-central1", this.parameterName, "v1")) {
      parameterManagerTemplate.deleteParameterVersion("us-central1", this.parameterName, "v1");
    }

    if (parameterManagerTemplate.parameterVersionExists(
        "us-central1", this.parameterName, this.versionName)) {
      parameterManagerTemplate.deleteParameterVersion(
          "us-central1", this.parameterName, this.versionName);
      parameterManagerTemplate.deleteParameter("us-central1", this.parameterName);
    } else if (parameterManagerTemplate.parameterExists("us-central1", this.parameterName)) {
      parameterManagerTemplate.deleteParameter("us-central1", this.parameterName);
    }
  }

  @Test
  void testCreateParameter() {
    MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
    params.add("parameterId", this.parameterName);
    params.add("versionId", "v1");
    params.add("projectId", "");
    params.add("locationId", "us-central1");
    params.add("parameterPayload", "{'message': 'Hello World 2!'}");
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, new HttpHeaders());

    ResponseEntity<String> response =
        this.testRestTemplate.postForEntity("/createParameter", request, String.class);
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
  }

  @Test
  void testReadParameterVersion() {
    String getParameterUrl =
        String.format(
            "/getParameter?locationId=%s&parameterId=%s&versionId=%s",
            "us-central1", this.parameterName, this.versionName);
    ResponseEntity<String> response =
        this.testRestTemplate.getForEntity(getParameterUrl, String.class);
    assertThat(response.getBody())
        .contains(
            String.format(
                "Parameter Version ID: %s | Value: {'message': 'Hello World'}", this.versionName));
  }

  @Test
  void testDeleteParameterVersion() {
    MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
    params.add("parameterId", this.parameterName);
    params.add("versionId", this.versionName);
    params.add("projectId", "");
    params.add("locationId", "us-central1");
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, new HttpHeaders());

    ResponseEntity<String> response =
        this.testRestTemplate.postForEntity("/deleteParameterVersion", request, String.class);
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
  }

  @Test
  void testDeleteParameter() {
    parameterManagerTemplate.deleteParameterVersion("us-central1", this.parameterName, this.versionName);
    MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
    params.add("parameterId", this.parameterName);
    params.add("projectId", "");
    params.add("locationId", "us-central1");
    HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(params, new HttpHeaders());

    ResponseEntity<String> response =
        this.testRestTemplate.postForEntity("/deleteParameter", request, String.class);
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
  }
}
