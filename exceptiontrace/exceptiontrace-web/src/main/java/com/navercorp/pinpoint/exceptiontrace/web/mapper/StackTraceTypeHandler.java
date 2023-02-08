package com.navercorp.pinpoint.exceptiontrace.web.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class StackTraceTypeHandler extends BaseTypeHandler<List<String>> {

    private static final Logger logger = LogManager.getLogger(StackTraceTypeHandler.class);

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement,
                                    int i,
                                    List<String> strings,
                                    JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, new Gson().toJson(strings));
    }

    @Override
    public List<String> getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return convertToList(resultSet.getString(s));
    }

    @Override
    public List<String> getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return convertToList(resultSet.getString(i));
    }

    @Override
    public List<String> getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return convertToList(callableStatement.getString(i));
    }

    private List<String> convertToList(String s) {
        if (StringUtils.isEmpty(s)) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(s, new TypeReference<List<String>>() {
            });
        } catch (IOException e) {
            logger.error(e);
        }
        return Collections.emptyList();
    }

}
