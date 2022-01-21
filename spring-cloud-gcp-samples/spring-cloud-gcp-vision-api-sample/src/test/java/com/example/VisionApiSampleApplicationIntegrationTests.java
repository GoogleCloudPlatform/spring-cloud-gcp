/*
 * Copyright 2017-2018 the original author or authors.
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.google.cloud.vision.v1.EntityAnnotation;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.ModelAndView;

/** This test sends images to the GCP Vision API and verifies the returned image annotations. */
//Please use "-Dit.vision=true" to enable the tests
@EnabledIfSystemProperty(named = "it.vision", matches = "true")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class VisionApiSampleApplicationIntegrationTests {

  private static final String LABEL_IMAGE_URL =
      "/extractLabels?imageUrl=classpath:static/boston-terrier.jpg";

  private static final String TEXT_IMAGE_URL =
      "/extractText?imageUrl=classpath:static/stop-sign.jpg";

  @Autowired private MockMvc mockMvc;

  @Test
  void testExtractTextFromImage() throws Exception {
    this.mockMvc
        .perform(get(TEXT_IMAGE_URL))
        .andDo(
            response -> {
              ModelAndView result = response.getModelAndView();
              String textFromImage = ((String) result.getModelMap().get("text")).trim();
              assertThat(textFromImage).isEqualTo("STOP");
            });
  }

  @Test
  void testClassifyImageLabels() throws Exception {
    this.mockMvc
        .perform(get(LABEL_IMAGE_URL))
        .andDo(
            response -> {
              ModelAndView result = response.getModelAndView();
              List<EntityAnnotation> annotations =
                  (List<EntityAnnotation>) result.getModelMap().get("annotations");

              List<String> annotationNames =
                  annotations.stream()
                      .map(annotation -> annotation.getDescription().toLowerCase().trim())
                      .collect(Collectors.toList());

              assertThat(annotationNames).contains("dog");
            });
  }
}
