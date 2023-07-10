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
package com.navercorp.pinpoint.exceptiontrace.web.config;

import com.navercorp.pinpoint.exceptiontrace.common.model.SpanEventException;
import com.navercorp.pinpoint.exceptiontrace.common.model.StackTraceElementWrapper;
import com.navercorp.pinpoint.exceptiontrace.web.mapper.StackTraceTypeHandler;
import com.navercorp.pinpoint.exceptiontrace.web.mapper.ValueViewTypeHandler;
import com.navercorp.pinpoint.exceptiontrace.web.model.ExceptionTraceSummary;
import com.navercorp.pinpoint.exceptiontrace.web.model.ExceptionTraceValueView;
import com.navercorp.pinpoint.exceptiontrace.web.util.ExceptionTraceQueryParameter;
import com.navercorp.pinpoint.metric.collector.config.MyBatisRegistryHandler;
import org.apache.ibatis.type.TypeAliasRegistry;
import org.apache.ibatis.type.TypeHandlerRegistry;

/**
 * @author intr3p1d
 */
public class ExceptionTraceRegistryHandler implements MyBatisRegistryHandler {
    @Override
    public void registerTypeAlias(TypeAliasRegistry typeAliasRegistry) {
        typeAliasRegistry.registerAlias("SpanEventException", SpanEventException.class);
        typeAliasRegistry.registerAlias("StackTraceElementWrapper", StackTraceElementWrapper.class);
        typeAliasRegistry.registerAlias("ExceptionTraceSummary", ExceptionTraceSummary.class);
        typeAliasRegistry.registerAlias("ExceptionTraceValueView", ExceptionTraceValueView.class);
        typeAliasRegistry.registerAlias("StackTraceTypeHandler", StackTraceTypeHandler.class);
        typeAliasRegistry.registerAlias("ValueViewTypeHandler", ValueViewTypeHandler.class);
        typeAliasRegistry.registerAlias("ExceptionTraceQueryParameter", ExceptionTraceQueryParameter.class);
    }

    @Override
    public void registerTypeHandler(TypeHandlerRegistry typeHandlerRegistry) {

    }
}
