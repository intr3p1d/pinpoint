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
package com.navercorp.pinpoint.exceptiontrace.web.util;

import com.navercorp.pinpoint.exceptiontrace.common.pinot.CLPSuffix;
import com.navercorp.pinpoint.exceptiontrace.common.pinot.PinotColumns;

/**
 * @author intr3p1d
 */
public enum ClpReplacedColumns {

    ERROR_MESSAGE_ENCODED_VARS(PinotColumns.ERROR_MESSAGE_ENCODED_VARS, ARRAY_SLICE_INT),
    ERROR_MESSAGE_DICTIONARY_VARS(PinotColumns.ERROR_MESSAGE_DICTIONARY_VARS, ARRAY_SLICE_STRING);

    private static final String ARRAY_SLICE_INT = "arraySliceInt";
    private static final String ARRAY_SLICE_STRING = "arraySliceString";

    private final PinotColumns columns;
    private final String sliceFunction;

    ClpReplacedColumns(PinotColumns columns, String sliceFunction) {
        this.columns = columns;
        this.sliceFunction = sliceFunction;
    }
}
