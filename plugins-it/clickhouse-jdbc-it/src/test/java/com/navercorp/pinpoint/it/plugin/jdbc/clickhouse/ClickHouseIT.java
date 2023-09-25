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
import com.navercorp.pinpoint.it.plugin.utils.TestcontainersOption;
import com.navercorp.pinpoint.it.plugin.utils.jdbc.JDBCTestConstants;
import com.navercorp.pinpoint.test.plugin.Dependency;
import com.navercorp.pinpoint.test.plugin.PinpointAgent;
import com.navercorp.pinpoint.test.plugin.PinpointConfig;
import com.navercorp.pinpoint.test.plugin.PluginTest;
import com.navercorp.pinpoint.test.plugin.shared.SharedDependency;
import com.navercorp.pinpoint.test.plugin.shared.SharedTestLifeCycleClass;
import org.testcontainers.containers.ClickHouseContainer;

/**
 * @author intr3p1d
 */
@PluginTest
@PinpointAgent(AgentPath.PATH)
@Dependency({"com.clickhouse:clickhouse-jdbc:[0.3.2-patch11]",
        "log4j:log4j:1.2.16", "org.slf4j:slf4j-log4j12:1.7.5",
        JDBCTestConstants.VERSION})
@PinpointConfig("pinpoint-mssql.config")
@SharedDependency({"com.microsoft.sqlserver:mssql-jdbc:7.0.0.jre8", PluginITConstants.VERSION, JDBCTestConstants.VERSION, TestcontainersOption.TEST_CONTAINER, TestcontainersOption.MSSQL})
public class ClickHouseIT {
    private static final String CLICKHOUSE_IMAGE = "yandex/clickhouse-server:20.8";
    private final ClickHouseContainer dockerContainer;

    public ClickHouseIT() {
        this.dockerContainer = new ClickHouseContainer(CLICKHOUSE_IMAGE);
    }
}
