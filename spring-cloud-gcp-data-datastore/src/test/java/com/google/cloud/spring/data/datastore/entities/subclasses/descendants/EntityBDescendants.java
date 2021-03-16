/*
 * Copyright 2017-2019 the original author or authors.
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

package com.google.cloud.spring.data.datastore.entities.subclasses.descendants;

import java.util.ArrayList;
import java.util.List;

import com.google.cloud.spring.data.datastore.core.mapping.Descendants;
import com.google.cloud.spring.data.datastore.core.mapping.DiscriminatorValue;
import com.google.cloud.spring.data.datastore.core.mapping.Entity;

@Entity(name = "A")
@DiscriminatorValue("B")
public class EntityBDescendants extends EntityADescendants {
	@Descendants
	private List<EntityCDescendants> entitiesC = new ArrayList<>();

	public void addEntityC(EntityCDescendants entityCDescendants) {
		this.entitiesC.add(entityCDescendants);
	}

	public List<EntityCDescendants> getEntitiesC() {
		return entitiesC;
	}
}
