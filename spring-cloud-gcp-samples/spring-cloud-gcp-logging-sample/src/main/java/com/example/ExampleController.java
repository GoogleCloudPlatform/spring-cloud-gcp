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

import net.logstash.logback.marker.Markers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sample REST Controller to demonstrate Stackdriver Logging.
 *
 * @author Chengyuan Zhao
 */
@RestController
public class ExampleController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExampleController.class);

	@GetMapping("/log")
	public String log() {
		String message = "This line was written to the log.";
		String secondMessage = "This line was also written to the log with the same Trace ID.";

		Marker m = Markers.append("test_key", "test_value")
				.and(Markers.append("test_key2", "test_value2"));

		LOGGER.info(m, message);
		LOGGER.info(m, secondMessage);
		return message;
	}
}
