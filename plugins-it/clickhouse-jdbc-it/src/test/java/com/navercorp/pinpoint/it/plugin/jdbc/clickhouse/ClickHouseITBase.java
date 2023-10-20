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
import com.navercorp.pinpoint.bootstrap.context.DatabaseInfo;
import com.navercorp.pinpoint.bootstrap.plugin.jdbc.JdbcUrlParserV2;
import com.navercorp.pinpoint.bootstrap.plugin.test.Expectations;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifier;
import com.navercorp.pinpoint.bootstrap.plugin.test.PluginTestVerifierHolder;
import com.navercorp.pinpoint.it.plugin.utils.jdbc.DriverProperties;
import com.navercorp.pinpoint.it.plugin.utils.jdbc.JDBCApi;
import com.navercorp.pinpoint.it.plugin.utils.jdbc.JDBCDriverClass;
import com.navercorp.pinpoint.it.plugin.utils.jdbc.template.DriverManagerDataSource;
import com.navercorp.pinpoint.plugin.jdbc.clickhouse.ClickHouseConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author intr3p1d
 */
public class ClickHouseITBase {
    private final Logger logger = LogManager.getLogger(getClass());
    static final String TABLE_NAME = "jdbc_example_basic";

    protected DriverProperties driverProperties;
    protected ClickHouseITHelper clickHouseITHelper;

    protected String DB_TYPE = ClickHouseConstants.CLICK_HOUSE.getName();
    protected String DB_EXECUTE_QUERY = ClickHouseConstants.CLICK_HOUSE_EXECUTE_QUERY.getName();

    protected String jdbcUrl;
    protected String databaseId;
    protected String databaseIdPassword;
    protected String databaseAddress;
    protected String databaseName;
    protected JDBCDriverClass jdbcDriverClass;
    protected JDBCApi jdbcApi;
    private DataSource dataSource;

    public void setup(
            DriverProperties driverProperties,
            JdbcUrlParserV2 jdbcUrlParser,
            JDBCDriverClass jdbcDriverClass,
            JDBCApi jdbcApi) {
        this.driverProperties = driverProperties;
        this.clickHouseITHelper = new ClickHouseITHelper(driverProperties);

        this.jdbcUrl = driverProperties.getUrl();

        DatabaseInfo databaseInfo = jdbcUrlParser.parse(jdbcUrl);

        this.databaseAddress = databaseInfo.getHost().get(0);
        this.databaseName = databaseInfo.getDatabaseId();

        this.databaseId = driverProperties.getUser();
        this.databaseIdPassword = driverProperties.getPassword();
        this.dataSource = new DriverManagerDataSource(jdbcUrl, databaseId, databaseIdPassword);

        this.jdbcDriverClass = jdbcDriverClass;
        this.jdbcApi = jdbcApi;

        try {
            Driver driver = jdbcDriverClass.getDriver().newInstance();
            DriverManager.registerDriver(driver);
        } catch (Exception e) {
            throw new RuntimeException("driver register error", e);
        }
    }

    private ClickHouseConnection getConnection() throws SQLException {
        return clickHouseITHelper.getConnection();
    }

    public void executeQueries() throws SQLException {

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
            logger.info(count);
        }

        String sql = "select * from " + TABLE_NAME;
        try (ClickHouseStatement stmt = conn.createStatement()) {
            // set max_result_rows = 3, result_overflow_mode = 'break'
            // or simply discard rows after the first 3 in read-only mode
            stmt.setMaxRows(3);
            count = 0;
            try (ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    count++;
                }
            }
            logger.info(count);
        }

        Method connect = jdbcApi.getDriver().getConnect();
        verifier.verifyTrace(Expectations.event(DB_TYPE, connect, null, databaseAddress, databaseName, Expectations.cachedArgs(jdbcUrl)));

        Method execute = jdbcApi.getPreparedStatement().getExecute();
        verifier.verifyTrace(Expectations.event(DB_EXECUTE_QUERY, execute, null, databaseAddress, databaseName, Expectations.sql(dropAndCreateQuery, null)));

        Method executeQuery = jdbcApi.getPreparedStatement().getExecuteQuery();
        verifier.verifyTrace(Expectations.event(DB_EXECUTE_QUERY, executeQuery, null, databaseAddress, databaseName, Expectations.sql(sql, null)));

    }
}
