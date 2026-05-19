package com.google.cloud.spring.autoconfigure.sql;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class DefaultCloudSqlJdbcInfoProviderTests {

    @Test
    void trimsInstanceConnectionNameWhenBuildingPostgresJdbcUrl() {
        GcpCloudSqlProperties properties = new GcpCloudSqlProperties();
        properties.setDatabaseName("test-database");
        properties.setInstanceConnectionName("tubular-bells:singapore:test-instance\r");

        DefaultCloudSqlJdbcInfoProvider provider =
                new DefaultCloudSqlJdbcInfoProvider(properties, DatabaseType.POSTGRESQL);

        assertThat(provider.getJdbcUrl())
                .isEqualTo(
                        "jdbc:postgresql://google/test-database?"
                                + "socketFactory=com.google.cloud.sql.postgres.SocketFactory"
                                + "&cloudSqlInstance=tubular-bells:singapore:test-instance");
    }
}
