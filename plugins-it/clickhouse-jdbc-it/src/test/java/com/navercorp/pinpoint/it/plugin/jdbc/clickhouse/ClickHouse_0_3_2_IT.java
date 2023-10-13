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
import com.navercorp.pinpoint.bootstrap.plugin.jdbc.JdbcUrlParserV2;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifier;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifierHolder;
import com.navercorp.pinpoint.it.plugin.utils.AgentPath;
import com.navercorp.pinpoint.it.plugin.utils.PluginITConstants;
import com.navercorp.pinpoint.it.plugin.utils.jdbc.DataBaseTestCase;
import com.navercorp.pinpoint.it.plugin.utils.jdbc.DefaultJDBCApi;
import com.navercorp.pinpoint.it.plugin.utils.jdbc.DriverProperties;
import com.navercorp.pinpoint.it.plugin.utils.jdbc.JDBCApi;
import com.navercorp.pinpoint.it.plugin.utils.jdbc.JDBCDriverClass;
import com.navercorp.pinpoint.it.plugin.utils.jdbc.JDBCTestConstants;
import com.navercorp.pinpoint.it.plugin.utils.jdbc.testcontainers.DatabaseContainers;
import com.navercorp.pinpoint.plugin.jdbc.clickhouse.ClickHouseJdbcUrlParser;
import com.navercorp.pinpoint.test.plugin.Dependency;
import com.navercorp.pinpoint.test.plugin.PinpointAgent;
import com.navercorp.pinpoint.test.plugin.PinpointConfig;
import com.navercorp.pinpoint.test.plugin.PluginTest;
import com.navercorp.pinpoint.test.plugin.shared.SharedDependency;
import com.navercorp.pinpoint.test.plugin.shared.SharedTestBeforeAllResult;
import com.navercorp.pinpoint.test.plugin.shared.SharedTestLifeCycleClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.sql.SQLException;
import java.util.Properties;

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
public class ClickHouse_0_3_2_IT extends DataBaseTestCase {

    private final static String CLICK_HOUSE = "CLICK_HOUSE";
    private final static String CLICK_HOUSE_EXECUTE_QUERY = "CLICK_HOUSE_EXECUTE_QUERY";

    private final Logger logger = LogManager.getLogger(getClass());
    protected static DriverProperties driverProperties = DatabaseContainers.readSystemProperties();
    private static URI uri;

    private final ClickHouseITHelper clickHouseITHelper = new ClickHouseITHelper(driverProperties);
    private final ClickHouseITBase clickHouseITBase = new ClickHouseITBase();


    private static JDBCDriverClass driverClass;
    private static JDBCApi jdbcApi;

    private static JdbcUrlParserV2 jdbcUrlParser;

    public static DriverProperties getDriverProperties() {
        return driverProperties;
    }

    @SharedTestBeforeAllResult
    public static void setBeforeAllResult(Properties beforeAllResult) {
    }

    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
        DriverProperties driverProperties = getDriverProperties();
        driverClass = new ClickHouseJDBCDriverClass();
        jdbcApi = new DefaultJDBCApi(driverClass);

        jdbcUrlParser = new ClickHouseJdbcUrlParser();
    }

    @Override
    protected JDBCDriverClass getJDBCDriverClass() {
        return driverClass;
    }

    @BeforeEach
    public void before() {
        logger.info("before");
        setup(CLICK_HOUSE, CLICK_HOUSE_EXECUTE_QUERY, driverProperties, jdbcUrlParser, jdbcApi);
    }

    private ClickHouseConnection getConnection() throws SQLException {
        return clickHouseITHelper.getConnection();
    }

    @Override
    public void testStatement() throws Exception {
        super.testStatement();
    }

    @Override
    public void testStoredProcedure_with_IN_OUT_parameters() throws Exception {
        super.testStoredProcedure_with_IN_OUT_parameters();
    }

    @Override
    public void testStoredProcedure_with_INOUT_parameters() throws Exception {
        super.testStoredProcedure_with_INOUT_parameters();
    }

    @Test
    public void testDropAndCreateTable() throws SQLException {
        clickHouseITBase.dropAndCreateTable();
    }

    @Test
    public void test1() throws SQLException {
        PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
        ClickHouseConnection conn = getConnection();
        clickHouseITBase.query(conn);
        logger.info(verifier.getExecutedMethod());
    }

    @Test
    public void test2() throws SQLException {
        PluginTestVerifier verifier = PluginTestVerifierHolder.getInstance();
        ClickHouseConnection conn = getConnection();
        clickHouseITBase.insertByteArray(conn);
        logger.info(verifier.getExecutedMethod());
    }

}
