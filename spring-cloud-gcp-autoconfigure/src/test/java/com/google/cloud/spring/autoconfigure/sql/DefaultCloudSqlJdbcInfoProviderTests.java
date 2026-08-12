/*
 * Copyright 2026 the original author or authors.
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
