package com.example;

import com.google.cloud.spring.data.spanner.core.admin.SpannerDatabaseAdminTemplate;
import com.google.cloud.spring.data.spanner.core.admin.SpannerSchemaUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@TestPropertySource("classpath:application-test.properties")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { SpannerExampleDriver.class })
public class TempTests {

    @Autowired
    private TestEntityRepository testEntityRepository;

    @Autowired
    private SpannerSchemaUtils spannerSchemaUtils;

    @Autowired
    private SpannerDatabaseAdminTemplate spannerDatabaseAdminTemplate;

    void createTablesIfNotExists() {
//		if (!this.spannerDatabaseAdminTemplate.tableExists("trades")) {
//			this.spannerDatabaseAdminTemplate.executeDdlStrings(
//					Arrays.asList(
//							this.spannerSchemaUtils.getCreateTableDdlString(Trade.class)),
//					true);
//		}

        if (!this.spannerDatabaseAdminTemplate.tableExists("test")) {
            this.spannerDatabaseAdminTemplate.executeDdlStrings(Arrays.asList(
                    this.spannerSchemaUtils.getCreateTableDdlString(TestEntity.class)), true);
        } else {
            this.spannerDatabaseAdminTemplate.executeDdlStrings(Arrays.asList(
                    this.spannerSchemaUtils.getDropTableDdlString(TestEntity.class)), false);
            this.spannerDatabaseAdminTemplate.executeDdlStrings(Arrays.asList(
                    this.spannerSchemaUtils.getCreateTableDdlString(TestEntity.class)), true);
        }
    }


    @Before
    @After
    public void cleanupAndSetupTables() {
        this.testEntityRepository.deleteAll();
    }

    @Test
    public void testPlayground() {

//		createTablesIfNotExists();
//		this.tradeRepository.deleteAll();
        TraderDetails details = new TraderDetails("address line", 5L , true);
//        this.testEntityRepository.save(new TestEntity("id1", "John"));
        this.testEntityRepository.save(new TestEntity("id1", "John", details));

        System.out.println(this.testEntityRepository.findById("id1").get().getName());
        System.out.println(this.testEntityRepository.findById("id1").get().getDetails());
    }
}
