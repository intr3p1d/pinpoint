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
package com.navercorp.pinpoint.exceptiontrace.web.util;

import com.navercorp.pinpoint.exceptiontrace.common.model.ExceptionMetaDataEntity;
import com.navercorp.pinpoint.exceptiontrace.common.model.ExceptionTraceValueViewEntity;
import com.navercorp.pinpoint.exceptiontrace.web.model.ExceptionTraceValueView;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;

import java.util.List;

import static com.navercorp.pinpoint.exceptiontrace.common.util.MapperUtils.convertToList;

/**
 * @author intr3p1d
 */
public class ValueViewMapperUtils {

    private ValueViewMapperUtils() {
    }

    public static ModelMapper newValueViewModelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        TypeMap<ExceptionTraceValueViewEntity, ExceptionTraceValueView> typeMap = modelMapper.createTypeMap(
                ExceptionTraceValueViewEntity.class, ExceptionTraceValueView.class
        );

        typeMap.addMappings(
                mapper -> mapper.map(ExceptionTraceValueViewEntity::getValues, ExceptionTraceValueView::setValues)
        );

        typeMap.addMappings(
                mapper -> mapper.skip(ExceptionTraceValueView::setGroupedFieldName)
        );

        typeMap.addMappings(mapper -> mapper.<String>map(ExceptionMetaDataEntity::getUriTemplate, (dest, v) -> dest.getGroupedFieldName().setUriTemplate(v)));
        typeMap.addMappings(mapper -> mapper.<String>map(ExceptionMetaDataEntity::getErrorClassName, (dest, v) -> dest.getGroupedFieldName().setErrorClassName(v)));
        typeMap.addMappings(mapper -> mapper.<String>map(ExceptionMetaDataEntity::getErrorMessage, (dest, v) -> dest.getGroupedFieldName().setErrorMessage(v)));
        typeMap.addMappings(mapper -> mapper.<String>map(ExceptionMetaDataEntity::getStackTraceHash, (dest, v) -> dest.getGroupedFieldName().setStackTraceHash(v)));


        modelMapper
                .addConverter(
                        (Converter<String, List<?>>) mappingContext -> convertToList(mappingContext.getSource())
                );

        modelMapper.validate();
        return modelMapper;
    }
}
