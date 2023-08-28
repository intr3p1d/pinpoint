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
import com.navercorp.pinpoint.common.server.util.json.Jackson;
import com.navercorp.pinpoint.common.util.StringUtils;
import com.navercorp.pinpoint.exceptiontrace.common.entity.ExceptionMetaDataEntity;
import com.navercorp.pinpoint.exceptiontrace.common.model.ExceptionMetaData;
import com.navercorp.pinpoint.exceptiontrace.common.model.StackTraceElementWrapper;
import com.navercorp.pinpoint.exceptiontrace.web.entity.ExceptionTraceSummaryEntity;
import com.navercorp.pinpoint.exceptiontrace.web.entity.ExceptionTraceValueViewEntity;
import com.navercorp.pinpoint.exceptiontrace.web.model.ExceptionTraceSummary;
import com.navercorp.pinpoint.exceptiontrace.web.model.ExceptionTraceValueView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author intr3p1d
 */
@Component
public class EntityToModelMapper {
    private static final Logger logger = LogManager.getLogger(EntityToModelMapper.class);
    private static final ObjectMapper objectMapper = Jackson.newMapper();

    private ModelMapper exceptionEntityToModel = newEntityToModelMapper();

    private ModelMapper valueViewEntityToModel = newValueViewEntityToModel();

    public EntityToModelMapper() {
    }

    public ExceptionMetaData toModel(ExceptionMetaDataEntity entity) {
        return exceptionEntityToModel.map(entity, ExceptionMetaData.class);
    }

    private ModelMapper newEntityToModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        Converter<ExceptionMetaDataEntity, List<StackTraceElementWrapper>> converter =
                ctx -> getWrappers(
                        ctx.getSource().getStackTraceClassName(),
                        ctx.getSource().getStackTraceFileName(),
                        ctx.getSource().getStackTraceLineNumber(),
                        ctx.getSource().getStackTraceMethodName()
                );

        modelMapper
                .createTypeMap(ExceptionMetaDataEntity.class, ExceptionMetaData.class)
                .addMappings(
                        mapping -> mapping.using(converter).map(claim -> claim, ExceptionMetaData::setStackTrace)
                );

        modelMapper
                .addConverter(
                        (Converter<String, List<?>>) mappingContext -> convertToList(mappingContext.getSource())
                );

        modelMapper.validate();
        return modelMapper;
    }


    private List<StackTraceElementWrapper> getWrappers(
            String classNames,
            String fileNames,
            String lineNumbers,
            String methodNames
    ) {

        List<String> classNameIterable = convertToList(classNames);
        List<String> fileNameIterable = convertToList(fileNames);
        List<Integer> lineNumberIterable = convertToList(lineNumbers);
        List<String> methodNameIterable = convertToList(methodNames);

        List<StackTraceElementWrapper> wrappers = new ArrayList<>();
        for (int i = 0; i < classNameIterable.size(); i++) {
            wrappers.add(
                    new StackTraceElementWrapper(
                            classNameIterable.get(i),
                            fileNameIterable.get(i),
                            lineNumberIterable.get(i),
                            methodNameIterable.get(i)
                    )
            );
        }
        return wrappers;
    }

    public <T> List<T> convertToList(String s) {
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

    public ExceptionTraceSummary toModel(ExceptionTraceSummaryEntity entity) {
        return valueViewEntityToModel.map(entity, ExceptionTraceSummary.class);
    }

    public ExceptionTraceValueView toModel(ExceptionTraceValueViewEntity entity) {
        return valueViewEntityToModel.map(entity, ExceptionTraceValueView.class);
    }

    private ModelMapper newValueViewEntityToModel() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);

        TypeMap<ExceptionTraceValueViewEntity, ExceptionTraceValueView> typeMap = modelMapper.createTypeMap(
                ExceptionTraceValueViewEntity.class, ExceptionTraceValueView.class
        );
        typeMap.addMappings(
                mapper -> mapper.using(
                        (MappingContext<String, List<Integer>> ctx)
                                -> convertToList(ctx.getSource())
                ).map(
                        ExceptionTraceValueViewEntity::getValues,
                        ExceptionTraceValueView::setValues
                )
        );

        modelMapper.validate();
        return modelMapper;
    }
}
