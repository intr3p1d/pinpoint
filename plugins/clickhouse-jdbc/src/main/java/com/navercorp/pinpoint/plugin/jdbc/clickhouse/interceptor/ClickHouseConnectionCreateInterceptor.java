/*
 * Copyright 2014 NAVER Corp.
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

package com.navercorp.pinpoint.plugin.jdbc.clickhouse.interceptor;

import java.util.Arrays;
import java.util.Properties;

import com.clickhouse.client.ClickHouseNode;
import com.clickhouse.client.ClickHouseNodes;
import com.clickhouse.jdbc.internal.ClickHouseJdbcUrlParser;
import com.navercorp.pinpoint.bootstrap.context.DatabaseInfo;
import com.navercorp.pinpoint.bootstrap.context.SpanEventRecorder;
import com.navercorp.pinpoint.bootstrap.context.Trace;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.interceptor.AroundInterceptor;
import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.bootstrap.plugin.jdbc.DatabaseInfoAccessor;
import com.navercorp.pinpoint.bootstrap.plugin.jdbc.DefaultDatabaseInfo;
import com.navercorp.pinpoint.bootstrap.util.InterceptorUtils;
import com.navercorp.pinpoint.common.util.ArrayArgumentUtils;
import com.navercorp.pinpoint.plugin.jdbc.clickhouse.ClickHouseConstants;
import com.navercorp.pinpoint.plugin.jdbc.clickhouse.interceptor.getter.DatabaseGetter;

/**
 * @author emeroad
 */
// #1375 Workaround java level Deadlock
// https://oss.navercorp.com/pinpoint/pinpoint-naver/issues/1375
// @TargetConstructor({ "java.lang.String", "int", "java.util.Properties", "java.lang.String", "java.lang.String" })
public class ClickHouseConnectionCreateInterceptor implements AroundInterceptor {

    private final PLogger logger = PLoggerFactory.getLogger(this.getClass());
    private final boolean isDebug = logger.isDebugEnabled();

    private final TraceContext traceContext;

    public ClickHouseConnectionCreateInterceptor(TraceContext traceContext) {
        this.traceContext = traceContext;
    }

    @Override
    public void after(Object target, Object[] args, Object result, Throwable throwable) {
        System.out.printf("\n\n\nSYSTEM OUT\n\n\n");
        if (isDebug) {
            logger.afterInterceptor(target, args, result, throwable);
        }
        String uri = ArrayArgumentUtils.getArgument(args, 0, String.class);
        String databaseId = getDatabase(target);

        String tmpUrl = uri.substring(uri.lastIndexOf("/") + 1);
        DatabaseInfo dbInfo = new DefaultDatabaseInfo(ClickHouseConstants.CLICK_HOUSE, ClickHouseConstants.CLICK_HOUSE_EXECUTE_QUERY, "tmpUrl", "tmpUrl", Arrays.asList("tmpUrl"), databaseId);

        logger.warn("\n\n\nSYSTEM OUT\n\n\n");
        logger.warn(uri);
        logger.warn(databaseId);
        logger.warn(tmpUrl);
        logger.warn(dbInfo.toString());
        logger.warn("\n\n\nSYSTEM OUT\n\n\n");


        if (args[0] instanceof ClickHouseJdbcUrlParser.ConnectionInfo) {
            ClickHouseJdbcUrlParser.ConnectionInfo connectionInfo = (ClickHouseJdbcUrlParser.ConnectionInfo) args[0];
            Properties properties = connectionInfo.getProperties();
            ClickHouseNodes nodes = connectionInfo.getNodes();
            ClickHouseNode node = nodes.getNodes().get(0);

            uri = node.getBaseUri();
            if (properties.getProperty("database") != null) {
                databaseId = properties.getProperty("database");
            }

            String tmpURL = uri.substring(uri.lastIndexOf("/") + 1);
            // It's dangerous to use this url directly
            dbInfo = createDatabaseInfo(tmpURL, databaseId);
        }

        if (InterceptorUtils.isSuccess(throwable)) {
            // Set only if connection is success.
            if (target instanceof DatabaseInfoAccessor) {
                ((DatabaseInfoAccessor) target)._$PINPOINT$_setDatabaseInfo(dbInfo);
            }
        }

        final Trace trace = traceContext.currentTraceObject();
        if (trace == null) {
            return;
        }

        SpanEventRecorder recorder = trace.currentSpanEventRecorder();
        // We must do this if current transaction is being recorded.
        if (dbInfo != null) {
            recorder.recordServiceType(dbInfo.getType());
            recorder.recordEndPoint(dbInfo.getMultipleHost());
            recorder.recordDestinationId(dbInfo.getDatabaseId());
        }
    }

    private DatabaseInfo createDatabaseInfo(String url, String databaseId) {
        return new DefaultDatabaseInfo(ClickHouseConstants.CLICK_HOUSE, ClickHouseConstants.CLICK_HOUSE_EXECUTE_QUERY, url, url, Arrays.asList(url), databaseId);
    }

    private String getDatabase(Object target) {
        if (target instanceof DatabaseGetter) {
            return ((DatabaseGetter) target)._$PINPOINT$_getDatabase();
        }
        return null;
    }

    @Override
    public void before(Object target, Object[] args) {
        // ignore
    }
}
