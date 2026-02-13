package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.spring.parametermanager.ParameterManagerTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.util.HtmlUtils;

/**
 * Tests sample application endpoint that loads parameters as properties into the application
 * context. Application parameter named "application-param" must exist and have a value of
 * "{'message': 'Hello World from GCP PM!!'}".
 */
@EnabledIfSystemProperty(named = "it.parametermanager", matches = "true")
@ExtendWith(SpringExtension.class)
@AutoConfigureTestRestTemplate
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = ParameterManagerApplication.class)
class ParameterManagerRegionalSampleLoadParametersIntegrationTests {

  private static final String PARAMETER_CONTENT = "{ \"message\": \"Hello Regional World from GCP PM!!\" }";
  @Autowired private ParameterManagerTemplate parameterManagerTemplate;
  @Autowired private TestRestTemplate testRestTemplate;

  @Test
  void testApplicationStartupParameterLoadsCorrectly() {
    ResponseEntity<String> response = testRestTemplate.getForEntity("/", String.class);
    assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    assertThat(response.getBody().replaceAll("\\s+", " "))
        .contains(
            ("<b>Application regional parameter from @Value:</b> <i>"
                    + HtmlUtils.htmlEscape(PARAMETER_CONTENT)
                    + "</i>")
                .replaceAll("\\s+", " "));
    assertThat(response.getBody().replaceAll("\\s+", " "))
        .contains(
            ("<b>Application regional parameter from @ConfigurationProperties:</b> <i>"
                    + HtmlUtils.htmlEscape(PARAMETER_CONTENT)
                    + "</i>")
                .replaceAll("\\s+", " "));
  }
}
