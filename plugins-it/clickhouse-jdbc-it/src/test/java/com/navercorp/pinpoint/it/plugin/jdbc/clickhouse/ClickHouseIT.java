/*
 * Copyright 2023 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.it.plugin.jdbc.clickhouse;

import com.navercorp.pinpoint.it.plugin.utils.AgentPath;
import com.navercorp.pinpoint.it.plugin.utils.PluginITConstants;
import com.navercorp.pinpoint.it.plugin.utils.jdbc.DriverProperties;
import com.navercorp.pinpoint.it.plugin.utils.jdbc.JDBCTestConstants;
import com.navercorp.pinpoint.it.plugin.utils.jdbc.testcontainers.DatabaseContainers;
import com.navercorp.pinpoint.test.plugin.Dependency;
import com.navercorp.pinpoint.test.plugin.PinpointAgent;
import com.navercorp.pinpoint.test.plugin.PinpointConfig;
import com.navercorp.pinpoint.test.plugin.PluginTest;
import com.navercorp.pinpoint.test.plugin.shared.SharedDependency;
import com.navercorp.pinpoint.test.plugin.shared.SharedTestLifeCycleClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author intr3p1d
 */
@PluginTest
@PinpointAgent(AgentPath.PATH)
@Dependency({"com.clickhouse:clickhouse-jdbc:[0.3.2-patch11]",
        "log4j:log4j:1.2.16", "org.slf4j:slf4j-log4j12:1.7.5",
        JDBCTestConstants.VERSION})
@PinpointConfig("pinpoint-clickhouse.config")
@SharedDependency({
        "com.clickhouse:clickhouse-jdbc:[0.3.2-patch11]",
        PluginITConstants.VERSION, JDBCTestConstants.VERSION,
        "org.testcontainers:testcontainers:1.19.0",
        "org.testcontainers:clickhouse:1.19.0"
})
@SharedTestLifeCycleClass(ClickHouseServer.class)
public class ClickHouseIT {
    protected static DriverProperties driverProperties = DatabaseContainers.readSystemProperties();
    static final String TABLE_NAME = "jdbc_example_basic";
    private static URI uri;

    public static DriverProperties getDriverProperties() {
        return driverProperties;
    }

    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
        DriverProperties driverProperties = getDriverProperties();
        uri = new URI(driverProperties.getUrl());
    }

    @Test
    public void test0() throws SQLException {
        Connection conn = getConnection("jdbc:ch://localhost", new Properties());
        dropAndCreateTable(conn);
        Assertions.assertEquals(1, 1);
    }

    private static Connection getConnection(String url, Properties properties) throws SQLException {
        final Connection conn;

        conn = DriverManager.getConnection(url, properties);
        System.out.println("Connected to: " + conn.getMetaData().getURL());
        return conn;
    }

    static int dropAndCreateTable(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // multi-statement query is supported by default
            // session will be created automatically during execution
            stmt.execute(String.format(
                    "drop table if exists %1$s; create table %1$s(a String, b Nullable(String)) engine=Memory",
                    TABLE_NAME));
            return stmt.getUpdateCount();
        }
    }

}
