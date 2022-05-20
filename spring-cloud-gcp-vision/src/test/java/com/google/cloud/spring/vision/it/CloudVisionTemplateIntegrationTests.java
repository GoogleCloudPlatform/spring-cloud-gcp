package com.google.cloud.spring.vision.it;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

import com.google.cloud.spring.vision.CloudVisionTemplate;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {VisionTestConfiguration.class})
public class CloudVisionTemplateIntegrationTests {

  @Autowired private CloudVisionTemplate cloudVisionTemplate;

  @BeforeClass
  public static void prepare() {
    assumeThat(System.getProperty("it.vision"))
        .as(
            "Vision Sample integration tests are disabled. "
                + "Please use '-Dit.vision=true' to enable them.")
        .isEqualTo("true");
  }

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
