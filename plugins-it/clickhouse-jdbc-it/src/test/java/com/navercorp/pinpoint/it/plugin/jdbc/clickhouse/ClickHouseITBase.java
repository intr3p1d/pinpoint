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

import com.clickhouse.jdbc.ClickHouseConnection;
import com.clickhouse.jdbc.ClickHouseStatement;
import com.navercorp.pinpoint.bootstrap.plugin.jdbc.JdbcUrlParserV2;
import com.navercorp.pinpoint.bootstrap.plugin.test.Expectations;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifier;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifierHolder;
import com.navercorp.pinpoint.it.plugin.utils.jdbc.DataBaseTestCase;
import com.navercorp.pinpoint.it.plugin.utils.jdbc.DefaultJDBCApi;
import com.navercorp.pinpoint.it.plugin.utils.jdbc.DriverProperties;
import com.navercorp.pinpoint.it.plugin.utils.jdbc.JDBCApi;
import com.navercorp.pinpoint.it.plugin.utils.jdbc.JDBCDriverClass;
import com.navercorp.pinpoint.it.plugin.utils.jdbc.testcontainers.DatabaseContainers;
import com.navercorp.pinpoint.plugin.jdbc.clickhouse.ClickHouseJdbcUrlParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

/**
 * @author intr3p1d
 */
public class ClickHouseITBase {
    private final Logger logger = LogManager.getLogger(getClass());
    static final String TABLE_NAME = "jdbc_example_basic";

    protected static DriverProperties driverProperties = DatabaseContainers.readSystemProperties();
    private final ClickHouseITHelper clickHouseITHelper = new ClickHouseITHelper(driverProperties);


    public static DriverProperties getDriverProperties() {
        return driverProperties;
    }

    private ClickHouseConnection getConnection() throws SQLException {
        return clickHouseITHelper.getConnection();
    }

    public void dropAndCreateTable() throws SQLException {

        PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
        verifier.printCache();

        ClickHouseConnection conn = getConnection();
        String dropAndCreateQuery = String.format(
                "drop table if exists %1$s; create table %1$s(a String, b Nullable(String)) engine=Memory",
                TABLE_NAME);

        int count;
        try (ClickHouseStatement stmt = conn.createStatement()) {
            // multi-statement query is supported by default
            // session will be created automatically during execution
            stmt.execute(dropAndCreateQuery);
            count = stmt.getUpdateCount();
        }

        JDBCApi jdbcApi = new DefaultJDBCApi(new ClickHouseJDBCDriverClass());

        Method connect = jdbcApi.getDriver().getConnect();
        logger.info(verifier.getExecutedMethod());
        verifier.verifyTrace(Expectations.event("CLICK_HOUSE", connect, null, "test", "test", Expectations.cachedArgs("test")));

        // Method execute = jdbcApi.getPreparedStatement().getExecute();
        // verifier.verifyTrace(Expectations.event("CLICK_HOUSE_EXECUTE_QUERY", execute, null, "test", "test", Expectations.sql(dropAndCreateQuery, null)));
    }

    public int query(ClickHouseConnection conn) throws SQLException {
        String sql = "select * from " + TABLE_NAME;
        try (ClickHouseStatement stmt = conn.createStatement()) {
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

    public void insertByteArray(ClickHouseConnection conn) throws SQLException {
        try (ClickHouseStatement s = conn.createStatement()) {
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
