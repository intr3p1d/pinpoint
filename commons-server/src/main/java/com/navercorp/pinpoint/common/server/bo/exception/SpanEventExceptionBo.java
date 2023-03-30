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
package com.navercorp.pinpoint.common.server.bo.exception;

import java.util.List;

/**
 * @author intr3p1d
 */
public class SpanEventExceptionBo {

    private List<ExceptionWrapperBo> exceptionWrappers;
    private long startTime;
    private long exceptionId;
    private String uriTemplate;

    public SpanEventExceptionBo() {
    }

    public List<ExceptionWrapperBo> getExceptionWrappers() {
        return exceptionWrappers;
    }

    public void setExceptionWrappers(List<ExceptionWrapperBo> exceptionWrappers) {
        this.exceptionWrappers = exceptionWrappers;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getExceptionId() {
        return exceptionId;
    }

    public void setExceptionId(long exceptionId) {
        this.exceptionId = exceptionId;
    }

    public String getUriTemplate() {
        return uriTemplate;
    }

    public void setUriTemplate(String uriTemplate) {
        this.uriTemplate = uriTemplate;
    }

    @Override
    public String toString() {
        return "SpanEventExceptionBo{" +
                "exceptionWrappers=" + exceptionWrappers +
                ", startTime=" + startTime +
                ", exceptionId=" + exceptionId +
                ", uriTemplate='" + uriTemplate + '\'' +
                '}';
    }
}
