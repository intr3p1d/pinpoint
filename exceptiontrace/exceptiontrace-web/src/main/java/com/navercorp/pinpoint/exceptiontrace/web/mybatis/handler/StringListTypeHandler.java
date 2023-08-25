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
package com.navercorp.pinpoint.exceptiontrace.web.mybatis.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.pinpoint.common.server.util.json.Jackson;
import com.navercorp.pinpoint.common.util.StringUtils;
import org.apache.hadoop.hbase.shaded.com.google.gson.Gson;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * @author intr3p1d
 */
public class StringListTypeHandler extends BaseTypeHandler<List<String>> {

    private static final Logger logger = LogManager.getLogger(StringListTypeHandler.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public void setNonNullParameter(
            PreparedStatement ps,
            int i,
            List<String> StackTraceElementWrappers,
            JdbcType jdbcType
    ) throws SQLException {
        ps.setString(i, new Gson().toJson(StackTraceElementWrappers));
    }

    @Override
    public List<String> getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        return convertToList(resultSet.getString(columnName));
    }

    @Override
    public List<String> getNullableResult(ResultSet resultSet, int columnIndex) throws SQLException {
        return convertToList(resultSet.getString(columnIndex));
    }

    @Override
    public List<String> getNullableResult(CallableStatement callableStatement, int columnIndex) throws SQLException {
        return convertToList(callableStatement.getString(columnIndex));
    }

    protected List<String> convertToList(String s) {
        if (StringUtils.isEmpty(s)) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(s, new TypeReference<>() {
            });
        } catch (IOException e) {
            logger.error(e);
        }
        return Collections.emptyList();
    }
}
