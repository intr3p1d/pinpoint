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

import com.yscope.clp.compressorfrontend.BuiltInVariableHandlingRuleVersions;
import com.yscope.clp.compressorfrontend.MessageDecoder;

import java.io.IOException;

/**
 * @author intr3p1d
 */
public class ErrorMessageMapper {

    public static final String LOGTYPE_COLUMN_SUFFIX = "_logtype";
    public static final String DICTIONARY_VARS_COLUMN_SUFFIX = "_dictionaryVars";
    public static final String ENCODED_VARS_COLUMN_SUFFIX = "_encodedVars";

    public final MessageDecoder decoder = new MessageDecoder(BuiltInVariableHandlingRuleVersions.VariablesSchemaV2,
                   BuiltInVariableHandlingRuleVersions.VariableEncodingMethodsV1);

    private String decodeMessage(String logType, String[] dictionaryVarsString, long[] encodedVarsString) throws IOException {

        long[] encodedVars;
        String[] dictionaryVars;
        return decoder.decodeMessage(
                logType, dictionaryVars, encodedVars
        );
    }

}
