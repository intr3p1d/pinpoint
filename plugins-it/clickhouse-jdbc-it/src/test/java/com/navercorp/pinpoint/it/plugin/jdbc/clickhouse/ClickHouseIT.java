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

import com.navercorp.pinpoint.bootstrap.plugin.test.ExpectedTrace;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifier;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifierHolder;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;

import static com.navercorp.pinpoint.bootstrap.plugin.test.Expectations.annotation;

/**
 * @author intr3p1d
 */
@PluginTest
@PinpointAgent(AgentPath.PATH)
@Dependency({
        "com.clickhouse:clickhouse-jdbc:[0.3.2-patch11]",
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
    private final Logger logger = LogManager.getLogger(getClass());
    protected static DriverProperties driverProperties = DatabaseContainers.readSystemProperties();
    static final String TABLE_NAME = "jdbc_example_basic";
    private static URI uri;

    private final ClickHouseITHelper clickHouseITHelper = new ClickHouseITHelper(driverProperties);

    public static DriverProperties getDriverProperties() {
        return driverProperties;
    }

    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
        DriverProperties driverProperties = getDriverProperties();
        uri = new URI(driverProperties.getUrl());
    }

    private Connection getConnection() throws SQLException {
        return clickHouseITHelper.getConnection();
    }

    @Test
    public void test0() throws SQLException {
        PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
        Connection conn = getConnection();
        dropAndCreateTable(conn);
        logger.info(verifier.getExecutedMethod());
    }

    private ExpectedTrace clientConnectionEvent() {
        ExpectedTrace.Builder eventBuilder = ExpectedTrace.createEventBuilder("CLICK_HOUSE");
        eventBuilder.setMethodSignature("ru.yandex.clickhouse");

        String remoteAddress = "127.0.0.1";
        eventBuilder.setEndPoint(remoteAddress);
        eventBuilder.setDestinationId(remoteAddress);
        eventBuilder.setAnnotations(annotation("http.url", "http://" + remoteAddress + "/" ));

        return eventBuilder.build();
    }

    @Test
    public void test1() throws SQLException {
        Connection conn = getConnection();
        query(conn);
    }

    @Test
    public void test2() throws SQLException {
        Connection conn = getConnection();
        insertByteArray(conn);
    }

    private int dropAndCreateTable(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // multi-statement query is supported by default
            // session will be created automatically during execution
            stmt.execute(String.format(
                    "drop table if exists %1$s; create table %1$s(a String, b Nullable(String)) engine=Memory",
                    TABLE_NAME));
            return stmt.getUpdateCount();
        }
    }


    private int query(Connection conn) throws SQLException {
        String sql = "select * from " + TABLE_NAME;
        try (Statement stmt = conn.createStatement()) {
            // set max_result_rows = 3, result_overflow_mode = 'break'
            // or simply discard rows after the first 3 in read-only mode
            stmt.setMaxRows(3);
            int count = 0;
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    count++;
                }
            }
            return count;
        }
    }

    private void insertByteArray(Connection conn) throws SQLException {
        try (Statement s = conn.createStatement()) {
            s.execute("drop table if exists t_map;"
                    + "CREATE TABLE t_map"
                    + "("
                    + "    `audit_seq` Int64 CODEC(Delta(8), LZ4),"
                    + "`timestamp` Int64 CODEC(Delta(8), LZ4),"
                    + "`event_type` LowCardinality(String),"
                    + "`event_subtype` LowCardinality(String),"
                    + "`actor_type` LowCardinality(String),"
                    + "`actor_id` String,"
                    + "`actor_tenant_id` LowCardinality(String),"
                    + "`actor_tenant_name` String,"
                    + "`actor_firstname` String,"
                    + "`actor_lastname` String,"
                    + "`resource_type` LowCardinality(String),"
                    + "`resource_id` String,"
                    + "`resource_container` LowCardinality(String),"
                    + "`resource_path` String,"
                    + "`origin_ip` String,"
                    + "`origin_app_name` LowCardinality(String),"
                    + "`origin_app_instance` String,"
                    + "`description` String,"
                    + "`attributes` Map(String, String)"
                    + ")"
                    + "ENGINE = MergeTree "
                    + "ORDER BY (resource_container, event_type, event_subtype) "
                    + "SETTINGS index_granularity = 8192");
            try (PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO t_map SETTINGS async_insert=1,wait_for_async_insert=1 VALUES (8481365034795008,1673349039830,'operation-9','a','service', 'bc3e47b8-2b34-4c1a-9004-123656fa0000','b', 'c', 'service-56','d', 'object','e', 'my-value-62', 'mypath', 'some.hostname.address.com', 'app-9', 'instance-6','x', ?)")) {
                stmt.setObject(1, Collections.singletonMap("key1", "value1"));
                stmt.execute();

                try (ResultSet rs = s.executeQuery("select attributes from t_map")) {
                    logger.info(rs.next());
                    logger.info(rs.getObject(1));
                }
            }
        }
    }

}
