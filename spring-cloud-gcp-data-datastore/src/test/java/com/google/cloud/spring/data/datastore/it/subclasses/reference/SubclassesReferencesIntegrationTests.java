package com.google.cloud.spring.data.datastore.it.subclasses.reference;

import com.google.cloud.spring.data.datastore.core.DatastoreTemplate;
import com.google.cloud.spring.data.datastore.entities.subclasses.reference.EntityAReference;
import com.google.cloud.spring.data.datastore.entities.subclasses.reference.EntityBReference;
import com.google.cloud.spring.data.datastore.entities.subclasses.reference.EntityCReference;
import com.google.cloud.spring.data.datastore.it.AbstractDatastoreIntegrationTests;
import com.google.cloud.spring.data.datastore.it.DatastoreIntegrationTestConfiguration;
import com.google.common.collect.Lists;
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
    public void deleteAll(){
        datastoreTemplate.deleteAll(EntityAReference.class);
        datastoreTemplate.deleteAll(EntityBReference.class);
        datastoreTemplate.deleteAll(EntityCReference.class);
    }

    @Test
    public void TestEntityCContainsReferenceToEntityB(){
        EntityBReference entityB_1 = new EntityBReference();
        EntityCReference entityC_1 = new EntityCReference(entityB_1);
        entityARepository.saveAll(Lists.newArrayList(entityB_1, entityC_1));
        EntityCReference fetchedC = (EntityCReference) entityARepository.findById(entityC_1.getId()).get();
        assertThat(fetchedC.getEntityB()).isNotNull();
    }

}
