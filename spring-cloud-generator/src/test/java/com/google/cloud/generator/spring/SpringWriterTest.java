/*
 * Copyright 2023 Google LLC
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

package com.google.cloud.generator.spring;

import static org.junit.Assert.assertEquals;

import com.google.api.generator.gapic.model.GapicContext;
import com.google.api.generator.test.framework.Assert;
import com.google.api.generator.test.framework.GoldenFileWriter;
import com.google.api.generator.test.protoloader.TestProtoLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Before;
import org.junit.Test;

public class SpringWriterTest {
  private GapicContext context;

  @Before
  public void setUp() {
    this.context = TestProtoLoader.instance().parseShowcaseEcho();
  }

  @Test
  public void buildAutoConfigRegistrationStringTest() {
    String result = SpringWriter.buildAutoConfigRegistrationString(context);
    String expected = "com.google.showcase.v1beta1.spring.EchoSpringAutoConfiguration";
    assertEquals(expected, result);
  }

  @Test
  public void buildSpringAdditionalMetadataJsonStringTest() {
    String result = SpringWriter.buildSpringAdditionalMetadataJsonString(context);
    String fileName = "SpringAdditionalMetadataJson.golden";
    GoldenFileWriter.saveCodegenToFile(this.getClass(), fileName, result);
    Path goldenFilePath = Paths.get(GoldenFileWriter.getGoldenDir(this.getClass()), fileName);
    Assert.assertCodeEquals(goldenFilePath, result);
  }

  @Test
  public void buildPomStringTest() {
    String result = SpringWriter.buildPomString(context);
    String fileName = "SpringPackagePom.golden";
    GoldenFileWriter.saveCodegenToFile(this.getClass(), fileName, result);
    Path goldenFilePath = Paths.get(GoldenFileWriter.getGoldenDir(this.getClass()), fileName);
    Assert.assertCodeEquals(goldenFilePath, result);
  }
}
