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
package com.navercorp.pinpoint.exceptiontrace.web.model;

import java.util.List;

/**
 * @author intr3p1d
 */
public class ClpConverted {

    private String errorMessage_logtype;
    private List<Long> errorMessage_encodedVars;
    private List<String> errorMessage_dictionaryVars;

    public ClpConverted() {
    }

    public String getErrorMessage_logtype() {
        return errorMessage_logtype;
    }

    public void setErrorMessage_logtype(String errorMessage_logtype) {
        this.errorMessage_logtype = errorMessage_logtype;
    }

    public List<Long> getErrorMessage_encodedVars() {
        return errorMessage_encodedVars;
    }

    public void setErrorMessage_encodedVars(List<Long> errorMessage_encodedVars) {
        this.errorMessage_encodedVars = errorMessage_encodedVars;
    }

    public List<String> getErrorMessage_dictionaryVars() {
        return errorMessage_dictionaryVars;
    }

    public void setErrorMessage_dictionaryVars(List<String> errorMessage_dictionaryVars) {
        this.errorMessage_dictionaryVars = errorMessage_dictionaryVars;
    }
}
