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

package com.google.cloud.spring.data.datastore.core.convert;

import java.util.List;
import java.util.Map;

import com.google.cloud.spring.data.datastore.core.mapping.Entity;
import com.google.cloud.spring.data.datastore.core.mapping.Unindexed;

/**
 * A test entity with an unindexed property and unindexed list property.
 *
 * @author Dmitry Solomakha
 * @author Chengyuan Zhao
 */
@Entity
class UnindexedTestDatastoreItem {
	private long indexedField;

	@Unindexed
	private long unindexedField;

	@Unindexed
	private List<String> unindexedStringListField;

	@Unindexed
	private Map<String, String> unindexedMapField;

	@Unindexed
	UnindexedTestDatastoreItem embeddedItem;

	@Unindexed
	List<UnindexedTestDatastoreItem> unindexedItems;

	UnindexedTestDatastoreItem(long indexedField, UnindexedTestDatastoreItem embeddedItem) {
		this.indexedField = indexedField;
		this.embeddedItem = embeddedItem;
	}

	UnindexedTestDatastoreItem() {
	}

	long getIndexedField() {
		return this.indexedField;
	}

	void setIndexedField(long indexedField) {
		this.indexedField = indexedField;
	}

	long getUnindexedField() {
		return this.unindexedField;
	}

	void setUnindexedField(long unindexedField) {
		this.unindexedField = unindexedField;
	}

	List<String> getUnindexedStringListField() {
		return this.unindexedStringListField;
	}

	void setUnindexedStringListField(List<String> unindexedStringListField) {
		this.unindexedStringListField = unindexedStringListField;
	}

	Map<String, String> getUnindexedMapField() {
		return this.unindexedMapField;
	}

	void setUnindexedMapField(Map<String, String> unindexedMapField) {
		this.unindexedMapField = unindexedMapField;
	}

	UnindexedTestDatastoreItem getEmbeddedItem() {
		return embeddedItem;
	}

	void setEmbeddedItem(UnindexedTestDatastoreItem embeddedItem) {
		this.embeddedItem = embeddedItem;
	}

	List<UnindexedTestDatastoreItem> getUnindexedItems() {
		return unindexedItems;
	}

	void setUnindexedItems(List<UnindexedTestDatastoreItem> unindexedItems) {
		this.unindexedItems = unindexedItems;
	}
}
