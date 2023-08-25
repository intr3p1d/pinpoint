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

import com.navercorp.pinpoint.exceptiontrace.common.model.ExceptionMetaData;
import com.navercorp.pinpoint.exceptiontrace.common.model.ExceptionMetaDataEntity;
import com.navercorp.pinpoint.exceptiontrace.common.model.StackTraceElementWrapper;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.DestinationSetter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author intr3p1d
 */
public class MapperUtils {

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
            DestinationSetter<ExceptionMetaDataEntity, V> EntitySetter
    ) {
        typeMap.addMappings(
                mapper -> mapper.using(
                        memberVariableConverter(wrapperGetter)
                ).map(ExceptionMetaData::getStackTrace, EntitySetter)
        );
    }

    private static <T> Converter<List<StackTraceElementWrapper>, Iterable<T>> memberVariableConverter(
            Function<StackTraceElementWrapper, T> getter
    ) {
        return ctx -> ctx.getSource().stream().map(getter).collect(Collectors.toList());
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

        modelMapper.validate();
        return modelMapper;
    }

    public static List<StackTraceElementWrapper> getWrappers(
            List<String> classNameIterable,
            List<String> fileNameIterable,
            List<Integer> lineNumberIterable,
            List<String> methodNameIterable
    ) {
        try {
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
        return null;
    }
}
