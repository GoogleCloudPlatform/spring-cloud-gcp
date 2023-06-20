/*
 * Copyright 2021-2022 the original author or authors.
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

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Web app controller class for sample application. Contains a function that runs a query and
 * displays the results.
 */
@RestController
public class WebController {

  private final R2dbcEntityTemplate template;

  public WebController(R2dbcEntityTemplate template) {
    this.template = template;
  }

  @GetMapping("/getTuples")
  public Flux<String> getTuples() {
    return template.select(User.class).all().map(user -> user.toString());
  }
}
