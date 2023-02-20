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

package com.navercorp.pinpoint.exceptiontrace.web.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.pinpoint.common.util.StringUtils;
import com.navercorp.pinpoint.exceptiontrace.common.model.StackTraceElementWrapper;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author intr3p1d
 */
public class StackTraceTypeHandler extends BaseTypeHandler<List<StackTraceElementWrapper>> {

    private static final Logger logger = LogManager.getLogger(StackTraceTypeHandler.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public void setNonNullParameter(
            PreparedStatement preparedStatement,
            int i,
            List<StackTraceElementWrapper> stackTraceElementWrappers,
            JdbcType jdbcType
    ) throws SQLException {
        preparedStatement.setString(i, new Gson().toJson(stackTraceElementWrappers));
    }

    @Override
    public List<StackTraceElementWrapper> getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return convertToList(resultSet.getString(s));
    }

    @Override
    public List<StackTraceElementWrapper> getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return convertToList(resultSet.getString(i));
    }

    @Override
    public List<StackTraceElementWrapper> getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return convertToList(callableStatement.getString(i));
    }

    protected List<StackTraceElementWrapper> convertToList(String s) {
        if (StringUtils.isEmpty(s)) {
            return Collections.emptyList();
        }
        try {
            List<String> strings = objectMapper.readValue(s, new TypeReference<List<String>>() {
            });
            List<StackTraceElementWrapper> stackTraceElementWrapperList = new ArrayList<>();
            for(String str : strings) {
                stackTraceElementWrapperList.add(objectMapper.readValue(str, StackTraceElementWrapper.class));
            }
            return stackTraceElementWrapperList;
        } catch (IOException e) {
            logger.error(e);
        }
        return Collections.emptyList();
    }

}
