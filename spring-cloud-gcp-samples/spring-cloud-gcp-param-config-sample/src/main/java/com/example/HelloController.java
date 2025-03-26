/*
 * Copyright 2025 Google LLC
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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Sample REST Controller to demonstrate Google Cloud Parameter Manager Integration. */
@RestController
public class HelloController {

  private final MyParamAppProperties sampleConfig;

  @Autowired
  public HelloController(MyParamAppProperties sampleConfig) {
    this.sampleConfig = sampleConfig;
  }

  @GetMapping("/")
  public String json() {
    String welcomeMessage = String.format(
        "Hello %s, your password is %s",
        this.sampleConfig.getUsername(),
        this.sampleConfig.getPassword()
    );
    return welcomeMessage;
  }
}
