/*
 * Copyright 2024 NAVER Corp.
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

import com.navercorp.pinpoint.common.server.mapper.MapStructUtils;
import com.navercorp.pinpoint.exceptiontrace.web.entity.ExceptionMetaDataEntity;
import com.yscope.clp.compressorfrontend.BuiltInVariableHandlingRuleVersions;
import com.yscope.clp.compressorfrontend.MessageDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * @author intr3p1d
 */
@Component
public class ErrorMessageMapper {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final MapStructUtils mapStructUtils;

    private final static MessageDecoder decoder = new MessageDecoder(
            BuiltInVariableHandlingRuleVersions.VariablesSchemaV2,
            BuiltInVariableHandlingRuleVersions.VariableEncodingMethodsV1
    );

    public ErrorMessageMapper(MapStructUtils mapStructUtils) {
        this.mapStructUtils = mapStructUtils;
    }

    @Qualifier
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.CLASS)
    public @interface DecodeCLPErrorMessage {

    }

    @DecodeCLPErrorMessage
    public String toErrorMessage(
            ExceptionMetaDataEntity entity
    ) {
        long[] encodedVars = toLongArray(
                mapStructUtils.jsonStrToList(entity.getErrorMessage_encodedVars())
        );
        String[] dictionaryVars = (String[]) mapStructUtils.jsonStrToList(entity.getErrorMessage_dictionaryVars()).toArray();

        try {
            return decode(entity.getErrorMessage_logtype(),
                    encodedVars,
                    dictionaryVars);
        } catch (Exception e) {
            logger.error("Mapping failed", e);
            return "";
        }
    }

    public String decode(
            String logtype,
            long[] encodedVars,
            String[] dictionaryVars
    ) throws IOException {
        return decoder.decodeMessage(
                logtype, dictionaryVars, encodedVars
        );
    }

    public static long[] toLongArray(List<Long> longList) {
        if (longList == null || longList.isEmpty()) {
            return new long[0];
        }

        int size = longList.size();
        long[] longArray = new long[size];

        for (int i = 0; i < size; i++) {
            longArray[i] = longList.get(i);
        }
        return longArray;
    }

    public static String[] toStringArray(List<String> stringList) {
        if (stringList == null || stringList.isEmpty()) {
            return new String[0];
        }

        int size = stringList.size();
        String[] stringArray = new String[size];

        for (int i = 0; i < size; i++) {
            stringArray[i] = stringList.get(i);
        }
        return stringArray;
    }
}
