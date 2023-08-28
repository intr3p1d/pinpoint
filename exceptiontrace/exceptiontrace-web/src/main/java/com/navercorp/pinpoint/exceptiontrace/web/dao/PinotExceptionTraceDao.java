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

package com.navercorp.pinpoint.exceptiontrace.web.dao;

import com.navercorp.pinpoint.exceptiontrace.common.model.ExceptionMetaData;
import com.navercorp.pinpoint.exceptiontrace.common.entity.ExceptionMetaDataEntity;
import com.navercorp.pinpoint.exceptiontrace.web.entity.ExceptionTraceValueViewEntity;
import com.navercorp.pinpoint.exceptiontrace.web.mapper.EntityToModelMapper;
import com.navercorp.pinpoint.exceptiontrace.web.model.ExceptionTraceSummary;
import com.navercorp.pinpoint.exceptiontrace.web.model.ExceptionTraceValueView;
import com.navercorp.pinpoint.exceptiontrace.web.util.ExceptionTraceQueryParameter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author intr3p1d
 */
@Repository
public class PinotExceptionTraceDao implements ExceptionTraceDao {
    private final Logger logger = LogManager.getLogger(this.getClass());

    private static final String NAMESPACE = PinotExceptionTraceDao.class.getName() + ".";

    private static final String SELECT_QUERY = "selectExceptions";
    private static final String SELECT_SUMMARIZED_QUERY = "selectSummarizedExceptions";
    private static final String SELECT_EXACT_QUERY = "selectExactException";
    private static final String SELECT_SUMMARIES_QUERY = "selectSummaries";
    private static final String SELECT_VALUEVIEWS_QUERY = "selectValueViews";

    private final SqlSessionTemplate sqlPinotSessionTemplate;

    private final EntityToModelMapper entityToModelMapper;

    public PinotExceptionTraceDao(
            @Qualifier("exceptionTracePinotSessionTemplate") SqlSessionTemplate sqlPinotSessionTemplate,
            EntityToModelMapper entityToModelMapper
    ) {
        this.sqlPinotSessionTemplate = Objects.requireNonNull(sqlPinotSessionTemplate, "sqlPinotSessionTemplate");
        this.entityToModelMapper = Objects.requireNonNull(entityToModelMapper, "entityToModelMapper");
    }

    @Override
    public List<ExceptionMetaData> getExceptions(ExceptionTraceQueryParameter exceptionTraceQueryParameter) {
        List<ExceptionMetaDataEntity> dataEntities = this.sqlPinotSessionTemplate.selectList(NAMESPACE + SELECT_QUERY, exceptionTraceQueryParameter);
        return dataEntities.stream().map(
                entityToModelMapper::toModel
        ).collect(Collectors.toList());
    }

    @Override
    public List<ExceptionMetaData> getSummarizedExceptions(ExceptionTraceQueryParameter exceptionTraceQueryParameter) {
        List<ExceptionMetaDataEntity> dataEntities = this.sqlPinotSessionTemplate.selectList(NAMESPACE + SELECT_SUMMARIZED_QUERY, exceptionTraceQueryParameter);
        return dataEntities.stream().map(
                entityToModelMapper::toModel
        ).collect(Collectors.toList());
    }

    @Override
    public ExceptionMetaData getException(ExceptionTraceQueryParameter exceptionTraceQueryParameter) {
        ExceptionMetaDataEntity entity = this.sqlPinotSessionTemplate.selectOne(NAMESPACE + SELECT_EXACT_QUERY, exceptionTraceQueryParameter);
        return entityToModelMapper.toModel(entity);
    }

    @Override
    public List<ExceptionTraceSummary> getSummaries(ExceptionTraceQueryParameter exceptionTraceQueryParameter) {
        return this.sqlPinotSessionTemplate.selectList(NAMESPACE + SELECT_SUMMARIES_QUERY, exceptionTraceQueryParameter);
    }

    @Override
    public List<ExceptionTraceValueView> getValueViews(ExceptionTraceQueryParameter exceptionTraceQueryParameter) {
        List<ExceptionTraceValueViewEntity> valueViewEntities = this.sqlPinotSessionTemplate.selectList(NAMESPACE + SELECT_VALUEVIEWS_QUERY, exceptionTraceQueryParameter);
        return valueViewEntities.stream().map(
                entityToModelMapper::toModel
        ).collect(Collectors.toList());
    }
}
