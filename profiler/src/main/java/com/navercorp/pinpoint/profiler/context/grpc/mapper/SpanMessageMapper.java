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
package com.navercorp.pinpoint.profiler.context.grpc.mapper;

import com.google.protobuf.StringValue;
import com.navercorp.pinpoint.common.profiler.logging.ThrottledLogger;
import com.navercorp.pinpoint.common.util.IntStringValue;
import com.navercorp.pinpoint.common.util.StringUtils;
import com.navercorp.pinpoint.grpc.trace.PAcceptEvent;
import com.navercorp.pinpoint.grpc.trace.PAnnotation;
import com.navercorp.pinpoint.grpc.trace.PIntStringValue;
import com.navercorp.pinpoint.grpc.trace.PParentInfo;
import com.navercorp.pinpoint.grpc.trace.PSpan;
import com.navercorp.pinpoint.grpc.trace.PSpanChunk;
import com.navercorp.pinpoint.grpc.trace.PSpanEvent;
import com.navercorp.pinpoint.io.SpanVersion;
import com.navercorp.pinpoint.profiler.context.Span;
import com.navercorp.pinpoint.profiler.context.SpanEvent;
import com.navercorp.pinpoint.profiler.context.compress.SpanProcessor;
import com.navercorp.pinpoint.profiler.context.grpc.GrpcAnnotationValueMapper;
import com.navercorp.pinpoint.profiler.context.grpc.config.SpanUriGetter;
import com.navercorp.pinpoint.profiler.context.id.Shared;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Condition;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.Objects;

/**
 * @author intr3p1d
 */
@Mapper(componentModel = MappingConstants.ComponentModel.JSR330,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {
                TraceIdMapStructUtils.class,
                SpanUriGetter.class,
        }
)
public interface SpanMessageMapper {

    @Mappings({
            @Mapping(source = ".", target = "version", qualifiedByName = "currentVersion"),

            @Mapping(source = "traceRoot.traceId.spanId", target = "spanId"),
            @Mapping(source = "traceRoot.traceId.parentSpanId", target = "parentSpanId"),

            @Mapping(source = "traceRoot.traceId.flags", target = "flag"),
            @Mapping(source = "traceRoot.shared.errorCode", target = "err"),

            @Mapping(source = "traceRoot.shared.loggingInfo", target = "loggingTransactionInfo"),
            @Mapping(target="exceptionInfo", ignore = true)

    })
    PSpan toProto(Span span);

    @Mappings({
            @Mapping(source = "traceRoot.shared", target = "rpc", qualifiedBy = SpanUriGetter.ToCollectedUri.class),
    })
    PAcceptEvent spanToAcceptEvent(Span span);

    @Mappings({
    })
    PSpanEvent spanEventToPSpanEvent(SpanEvent spanEvent);

    default StringValue map(String value) {
        return StringValue.of(value);
    }

    @Named("currentVersion")
    default int currentVersion(Span span) {
        return SpanVersion.TRACE_V2;
    }

    @Condition
    default boolean isNotEmpty(String value) {
        return !StringUtils.isEmpty(value);
    }

}
