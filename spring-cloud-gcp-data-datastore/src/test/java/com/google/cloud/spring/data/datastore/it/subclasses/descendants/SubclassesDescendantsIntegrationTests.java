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

package com.google.cloud.spring.data.datastore.it.subclasses.descendants;

import java.util.Arrays;
import java.util.List;

import com.google.cloud.spring.data.datastore.core.DatastoreTemplate;
import com.google.cloud.spring.data.datastore.entities.subclasses.descendants.EntityBDescendants;
import com.google.cloud.spring.data.datastore.entities.subclasses.descendants.EntityCDescendants;
import com.google.cloud.spring.data.datastore.entities.subclasses.reference.EntityAReference;
import com.google.cloud.spring.data.datastore.entities.subclasses.reference.EntityBReference;
import com.google.cloud.spring.data.datastore.entities.subclasses.reference.EntityCReference;
import com.google.cloud.spring.data.datastore.it.AbstractDatastoreIntegrationTests;
import com.google.cloud.spring.data.datastore.it.DatastoreIntegrationTestConfiguration;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assume.assumeThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { DatastoreIntegrationTestConfiguration.class })
public class SubclassesDescendantsIntegrationTests extends AbstractDatastoreIntegrationTests {

	@Autowired
	EntityADescendantsRepository entityARepository;

	@SpyBean
	private DatastoreTemplate datastoreTemplate;

	@BeforeClass
	public static void checkToRun() {
		assumeThat(
				"Datastore integration tests are disabled. Please use '-Dit.datastore=true' "
						+ "to enable them. ",
				System.getProperty("it.datastore"), is("true"));
	}

	@After
	public void deleteAll() {
		datastoreTemplate.deleteAll(EntityAReference.class);
		datastoreTemplate.deleteAll(EntityBReference.class);
		datastoreTemplate.deleteAll(EntityCReference.class);
	}

	@Test
	public void TestEntityCContainsReferenceToEntityB() {
		EntityBDescendants entityB_1 = new EntityBDescendants();
		EntityCDescendants entityC_1 = new EntityCDescendants();
		entityB_1.addEntityC(entityC_1);
		entityARepository.saveAll(Arrays.asList(entityB_1, entityC_1));
		EntityBDescendants fetchedB = (EntityBDescendants) entityARepository.findById(entityB_1.getId())
				.get();
		List<EntityCDescendants> entitiesCOfB = fetchedB.getEntitiesC();
		assertThat(entitiesCOfB).hasSize(1);
	}

}
