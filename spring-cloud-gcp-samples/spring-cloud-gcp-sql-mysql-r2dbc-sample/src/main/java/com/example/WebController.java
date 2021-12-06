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

import java.util.List;

import io.r2dbc.spi.ConnectionFactory;
import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Web app controller class for sample application. Contains a function that runs a query
 * and displays the results.
 *
 */
@RestController
public class WebController {

	private final ConnectionFactory connectionFactory;

	public WebController(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	@GetMapping("/getTuples")
	public List<String> getTuples() {
		String resultTemplate = "[%s, %s, %s]";
		return Mono.from(connectionFactory.create())
				.flatMapMany(connection -> connection.createStatement("SELECT * FROM users").execute())
				.flatMap(result -> result
						.map((row, metadata) -> String.format(resultTemplate, row.get("EMAIL", String.class),
								row.get("FIRST_NAME", String.class), row.get("LAST_NAME", String.class))))
				.collectList().block();
	}
}
