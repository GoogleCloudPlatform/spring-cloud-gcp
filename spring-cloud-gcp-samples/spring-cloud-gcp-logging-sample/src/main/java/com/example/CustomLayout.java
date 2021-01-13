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

import java.util.Iterator;
import java.util.Map;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.google.cloud.spring.logging.StackdriverJsonLayout;
import net.logstash.logback.marker.ObjectAppendingMarker;
import org.slf4j.Marker;

public class CustomLayout extends StackdriverJsonLayout {

	@Override
	protected void addCustomDataToJsonMap(Map<String, Object> jsonMap, ILoggingEvent event) {
		addLogstashMarkerIfNecessary(jsonMap, event.getMarker());
	}

	private void addLogstashMarkerIfNecessary(Map<String, Object> jsonMap, Marker marker) {
		if (marker == null) {
			return;
		}

		if (marker instanceof ObjectAppendingMarker) {
			ObjectAppendingMarker objectAppendingMarker = (ObjectAppendingMarker) marker;
			jsonMap.put(objectAppendingMarker.getFieldName(), objectAppendingMarker.getFieldValue());
		}

		if (marker.hasReferences()) {
			for (Iterator<?> i = marker.iterator(); i.hasNext(); ) {
				Marker next = (Marker) i.next();
				addLogstashMarkerIfNecessary(jsonMap, next);
			}
		}
	}
}
