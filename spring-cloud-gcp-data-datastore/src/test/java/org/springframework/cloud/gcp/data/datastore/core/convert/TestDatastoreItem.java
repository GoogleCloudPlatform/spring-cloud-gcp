/*
 *  Copyright 2018 original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.springframework.cloud.gcp.data.datastore.core.convert;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Blob;
import com.google.cloud.datastore.LatLng;

/**
 * @author Dmitry Solomakha
 */
class TestDatastoreItem {
	private String stringField;

	private Boolean boolField;

	private Double doubleField;

	private Long longField;

	private LatLng latLngField;

	private Timestamp timestampField;

	private Blob blobField;

	public String getStringField() {
		return this.stringField;
	}

	public void setStringField(String stringField) {
		this.stringField = stringField;
	}

	public Boolean getBoolField() {
		return this.boolField;
	}

	public void setBoolField(Boolean boolField) {
		this.boolField = boolField;
	}

	public Double getDoubleField() {
		return this.doubleField;
	}

	public void setDoubleField(Double doubleField) {
		this.doubleField = doubleField;
	}

	public Long getLongField() {
		return this.longField;
	}

	public void setLongField(Long longField) {
		this.longField = longField;
	}

	public LatLng getLatLngField() {
		return this.latLngField;
	}

	public void setLatLngField(LatLng latLngField) {
		this.latLngField = latLngField;
	}

	public Timestamp getTimestampField() {
		return this.timestampField;
	}

	public void setTimestampField(Timestamp timestampField) {
		this.timestampField = timestampField;
	}

	public Blob getBlobField() {
		return this.blobField;
	}

	public void setBlobField(Blob blobField) {
		this.blobField = blobField;
	}
}
