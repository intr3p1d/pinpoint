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
package com.navercorp.pinpoint.exceptiontrace.collector.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.pinpoint.common.server.util.json.Jackson;
import com.navercorp.pinpoint.exceptiontrace.common.entity.ExceptionMetaDataEntity;
import com.navercorp.pinpoint.exceptiontrace.common.model.ExceptionMetaData;
import com.navercorp.pinpoint.exceptiontrace.common.model.StackTraceElementWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.spi.DestinationSetter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author intr3p1d
 */
@Component
public class ModelToEntityMapper {
    private static final Logger logger = LogManager.getLogger(ModelToEntityMapper.class);
    private static final ObjectMapper objectMapper = Jackson.newMapper();

    private ModelMapper modelToEntity = newModelToEntityMapper();

    public ModelToEntityMapper() {
    }

    public ExceptionMetaDataEntity toEntity(ExceptionMetaData model) {
        return modelToEntity.map(model, ExceptionMetaDataEntity.class);
    }

    private ModelMapper newModelToEntityMapper() {
        ModelMapper modelMapper = new ModelMapper();

        TypeMap<ExceptionMetaData, ExceptionMetaDataEntity> typeMap = modelMapper.createTypeMap(
                ExceptionMetaData.class, ExceptionMetaDataEntity.class
        );

        addMappings(typeMap, StackTraceElementWrapper::getClassName, ExceptionMetaDataEntity::setStackTraceClassName);
        addMappings(typeMap, StackTraceElementWrapper::getFileName, ExceptionMetaDataEntity::setStackTraceFileName);
        addMappings(typeMap, StackTraceElementWrapper::getLineNumber, ExceptionMetaDataEntity::setStackTraceLineNumber);
        addMappings(typeMap, StackTraceElementWrapper::getMethodName, ExceptionMetaDataEntity::setStackTraceMethodName);

        modelMapper.validate();
        return modelMapper;
    }

    private <T, V> void addMappings(
            TypeMap<ExceptionMetaData, ExceptionMetaDataEntity> typeMap,
            Function<StackTraceElementWrapper, T> wrapperGetter,
            DestinationSetter<ExceptionMetaDataEntity, V> entitySetter
    ) {
        typeMap.addMappings(
                mapper -> mapper.using(
                        memberVariableFlattener(wrapperGetter)
                ).map(ExceptionMetaData::getStackTrace, entitySetter)
        );
    }

    private <T> Converter<List<StackTraceElementWrapper>, String> memberVariableFlattener(
            Function<StackTraceElementWrapper, T> getter
    ) {
        return ctx -> {
            try {
                return objectMapper.writeValueAsString(ctx.getSource().stream().map(getter).collect(Collectors.toList()));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
