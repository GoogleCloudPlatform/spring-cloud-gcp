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

package com.google.cloud.spring.data.spanner.test.domain;

import org.springframework.beans.factory.annotation.Value;

/**
 * A projection interface used for integration tests.
 *
 * @author Chengyuan Zhao
 */
public interface TradeProjection {

	String getAction();

	@Value("#{target.symbol + ' ' + target.action}")
	String getSymbolAndAction();
}
