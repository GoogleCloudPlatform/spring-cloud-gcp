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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * This test verifies that the jpa-sample works.
 *
 * <p>Run with: mvn -Dit.cloudsql test
 *
 * <p>The test will inherit the properties set in resources/application.properties.
 */
// Please use "-Dit.cloudsql=true" to enable the tests
@EnabledIfSystemProperty(named = "it.cloudsql", matches = "true")
@ExtendWith(SpringExtension.class)
@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest(classes = {DemoApplication.class})
class CloudSqlJpaSampleApplicationIntegrationTests {

  @Autowired private CommandLineRunner commandLineRunner;

  /** Used to check exception messages and types. */
  @Test
  void basicTest(CapturedOutput capturedOutput) throws Exception {
    // we need to run the command line runner again to capture output
    this.commandLineRunner.run();

    assertThat(capturedOutput.toString()).contains("Number of houses is 4");
    assertThat(capturedOutput.toString()).contains("636 Avenue of the Americas, NYC");
  }
}
