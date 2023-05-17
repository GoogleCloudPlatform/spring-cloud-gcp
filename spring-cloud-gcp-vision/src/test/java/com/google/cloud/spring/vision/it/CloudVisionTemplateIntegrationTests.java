package com.google.cloud.spring.vision.it;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.spring.vision.CloudVisionTemplate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@EnabledIfSystemProperty(named = "it.vision", matches = "true")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {VisionTestConfiguration.class})
public class CloudVisionTemplateIntegrationTests {

  @Autowired private CloudVisionTemplate cloudVisionTemplate;

  @Test
  public void testExtractTextFromSinglePagePdf() {
    Resource dummyPdf = new ClassPathResource("documents/single-page-dummy.pdf");

    List<String> extractedTexts = cloudVisionTemplate.extractTextFromPdf(dummyPdf);

    assertThat(extractedTexts).hasSize(1).contains("Dummy PDF File");
  }

  @Test
  public void testExtractTextFromMultiPagePdf() {
    Resource dummyPdf = new ClassPathResource("documents/multi-page-dummy.pdf");

    List<String> extractedTexts = cloudVisionTemplate.extractTextFromPdf(dummyPdf);

    assertThat(extractedTexts)
        .hasSize(2)
        .contains("Dummy PDF File Page 1")
        .contains("Dummy PDF File Page 2");
  }
}
