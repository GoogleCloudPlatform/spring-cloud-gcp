package com.google.cloud.spring.data.datastore.it;

import com.google.cloud.spring.data.datastore.core.DatastoreTemplate;
import com.google.cloud.spring.data.datastore.entities.EntityA;
import com.google.cloud.spring.data.datastore.entities.EntityB;
import com.google.cloud.spring.data.datastore.entities.EntityC;
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
public class SubclassesReferencesIntegrationTests extends AbstractDatastoreIntegrationTests{

    @Autowired
    EntityARepository entityARepository;

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
        datastoreTemplate.deleteAll(EntityA.class);
        datastoreTemplate.deleteAll(EntityB.class);
        datastoreTemplate.deleteAll(EntityC.class);
    }

    @Test
    public void TestEntityCContainsReferenceToEntityB(){
        EntityB entityB_1 = new EntityB();
        EntityC entityC_1 = new EntityC(entityB_1);
        entityARepository.saveAll(Lists.newArrayList(entityB_1, entityC_1));
        EntityC fetchedC = (EntityC) entityARepository.findById(entityC_1.getId()).get();
        assertThat(fetchedC.getEntityB()).isNotNull();
    }

    @Test
    public void TestEntityBContainsDescendantsToEntityC(){
        EntityB entityB_1 = new EntityB();
        EntityC entityC_1 = new EntityC(entityB_1);
        entityB_1.addEntityC(entityC_1);
        entityARepository.saveAll(Lists.newArrayList(entityB_1, entityC_1));
        EntityB fetchedB = (EntityB) entityARepository.findById(entityB_1.getId()).get();
        assertThat(fetchedB.getEntitiesC()).containsExactly(entityC_1);
    }

}
