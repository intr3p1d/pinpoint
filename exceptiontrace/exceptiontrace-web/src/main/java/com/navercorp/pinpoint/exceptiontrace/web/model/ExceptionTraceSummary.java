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
package com.navercorp.pinpoint.exceptiontrace.web.model;

/**
 * @author intr3p1d
 */
public class ExceptionTraceSummary {
    private final long timestamp;
    private final String errorClassName;
    private final String errorMessage;
    private final String stackTraceHash;
    private final long count;

    public ExceptionTraceSummary(long timestamp, String errorClassName, String errorMessage, String stackTraceHash, long count) {
        this.timestamp = timestamp;
        this.errorClassName = errorClassName;
        this.errorMessage = errorMessage;
        this.stackTraceHash = stackTraceHash;
        this.count = count;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getErrorClassName() {
        return errorClassName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getStackTraceHash() {
        return stackTraceHash;
    }

    public long getCount() {
        return count;
    }
}
