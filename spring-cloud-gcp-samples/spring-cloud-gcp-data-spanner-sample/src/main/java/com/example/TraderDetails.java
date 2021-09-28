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

import java.util.Objects;

public class TraderDetails {
	private String address;

	private Long yoe;

	private Boolean active;

	public TraderDetails(String address, Long yoe, Boolean active) {
		this.address = address;
		this.yoe = yoe;
		this.active = active;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		TraderDetails that = (TraderDetails) o;
		return active == that.active && Objects.equals(address, that.address) && Objects.equals(yoe, that.yoe);
	}

	@Override
	public int hashCode() {
		return Objects.hash(address, yoe, active);
	}

	@Override
	public String toString() {
		return "TraderDetails{" +
				"address='" + address + '\'' +
				", yoe=" + yoe +
				", active=" + active +
				'}';
	}
}
