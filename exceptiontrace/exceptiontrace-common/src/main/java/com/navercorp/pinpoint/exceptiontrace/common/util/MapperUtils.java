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
package com.navercorp.pinpoint.exceptiontrace.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.pinpoint.common.server.util.json.Jackson;
import com.navercorp.pinpoint.common.util.StringUtils;
import com.navercorp.pinpoint.exceptiontrace.common.model.ExceptionMetaData;
import com.navercorp.pinpoint.exceptiontrace.common.model.ExceptionMetaDataEntity;
import com.navercorp.pinpoint.exceptiontrace.common.model.StackTraceElementWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.DestinationSetter;
import org.modelmapper.spi.MappingContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_FLOAT_AS_INT;

/**
 * @author intr3p1d
 */
public class MapperUtils {

    private static final Logger logger = LogManager.getLogger(MapperUtils.class);
    private static final ObjectMapper objectMapper = Jackson.newMapper();

    public static ModelMapper newModelToEntityMapper() {
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

    private static <T, V> void addMappings(
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

    private static <T> Converter<List<StackTraceElementWrapper>, String> memberVariableFlattener(
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

    public static ModelMapper newEntityToModelMapper() {
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

    public static List<StackTraceElementWrapper> getWrappers(
            String classNames,
            String fileNames,
            String lineNumbers,
            String methodNames

    ) {
        try {
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
        } catch (Exception e) {
            // ignored
        }
        return Collections.emptyList();
    }

    public static <T> List<T> convertToList(String s) {
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
