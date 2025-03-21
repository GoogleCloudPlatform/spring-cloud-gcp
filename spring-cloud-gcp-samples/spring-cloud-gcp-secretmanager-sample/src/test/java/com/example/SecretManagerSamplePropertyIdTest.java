package com.example;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.spring.core.GcpProjectIdProvider;
import com.google.cloud.spring.secretmanager.SecretManagerTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@EnabledIfSystemProperty(named = "it.secretmanager", matches = "true")
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = SecretManagerApplication.class)
@TestPropertySource(
    properties = {
      "spring.cloud.gcp.secretmanager.project-id=spring-cloud-gcp-ci",
      "spring.cloud.gcp.project-id=gcp-project-id"
    })
@DisabledInAotMode
class SecretManagerSamplePropertyIdTest {

  @Autowired private SecretManagerTemplate secretManagerTemplate;

  @Autowired private GcpProjectIdProvider gcpProjectIdProvider;

  // This test verifies propertyId is correctly recognized.
  // When secretmanager.project-id and project gcp.project-id are both set,
  // secretManagerTemplate should recognize secretmanager.project-id
  // but gcpProjectIdProvider should only capture gcp.project-id
  @Test
  void testProjectIdCorrect() {
    String projectId = secretManagerTemplate.getProjectId();
    assertThat(projectId).isEqualTo("spring-cloud-gcp-ci");
    String gcpProjectId = gcpProjectIdProvider.getProjectId();
    assertThat(gcpProjectId).isEqualTo("gcp-project-id");
  }
}
