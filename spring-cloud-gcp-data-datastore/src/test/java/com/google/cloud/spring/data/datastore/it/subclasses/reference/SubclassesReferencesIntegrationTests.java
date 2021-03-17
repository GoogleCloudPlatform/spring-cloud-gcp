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

package com.google.cloud.spring.data.datastore.it.subclasses.reference;

import java.util.Arrays;

import com.google.cloud.spring.data.datastore.core.DatastoreTemplate;
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
public class SubclassesReferencesIntegrationTests extends AbstractDatastoreIntegrationTests {

	@Autowired
	EntityAReferenceRepository entityARepository;

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
		EntityBReference entityB_1 = new EntityBReference();
		EntityCReference entityC_1 = new EntityCReference(entityB_1);
		entityARepository.saveAll(Arrays.asList(entityB_1, entityC_1));
		EntityCReference fetchedC = (EntityCReference) entityARepository.findById(entityC_1.getId()).get();
		assertThat(fetchedC.getEntityB()).isNotNull();
	}

}
