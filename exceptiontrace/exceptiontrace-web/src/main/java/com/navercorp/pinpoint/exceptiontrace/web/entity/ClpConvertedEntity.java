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
package com.navercorp.pinpoint.exceptiontrace.web.entity;

/**
 * @author intr3p1d
 */
public class ClpConvertedEntity {

    private String errorMessage_logtype;
    private String errorMessage_encodedVars;
    private String errorMessage_dictionaryVars;

    public ClpConvertedEntity() {
    }

    public String getErrorMessage_logtype() {
        return errorMessage_logtype;
    }

    public void setErrorMessage_logtype(String errorMessage_logtype) {
        this.errorMessage_logtype = errorMessage_logtype;
    }

    public String getErrorMessage_encodedVars() {
        return errorMessage_encodedVars;
    }

    public void setErrorMessage_encodedVars(String errorMessage_encodedVars) {
        this.errorMessage_encodedVars = errorMessage_encodedVars;
    }

    public String getErrorMessage_dictionaryVars() {
        return errorMessage_dictionaryVars;
    }

    public void setErrorMessage_dictionaryVars(String errorMessage_dictionaryVars) {
        this.errorMessage_dictionaryVars = errorMessage_dictionaryVars;
    }
}
