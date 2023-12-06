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


import com.navercorp.pinpoint.common.util.StringUtils;
import com.navercorp.pinpoint.grpc.trace.PAcceptEvent;
import com.navercorp.pinpoint.grpc.trace.PAnnotation;
import com.navercorp.pinpoint.grpc.trace.PAnnotationValue;
import com.navercorp.pinpoint.grpc.trace.PLocalAsyncId;
import com.navercorp.pinpoint.grpc.trace.PParentInfo;
import com.navercorp.pinpoint.grpc.trace.PSpan;
import com.navercorp.pinpoint.grpc.trace.PSpanChunk;
import com.navercorp.pinpoint.grpc.trace.PSpanEvent;
import com.navercorp.pinpoint.io.SpanVersion;
import com.navercorp.pinpoint.profiler.context.Annotation;
import com.navercorp.pinpoint.profiler.context.AsyncSpanChunk;
import com.navercorp.pinpoint.profiler.context.LocalAsyncId;
import com.navercorp.pinpoint.profiler.context.Span;
import com.navercorp.pinpoint.profiler.context.SpanChunk;
import com.navercorp.pinpoint.profiler.context.SpanEvent;
import com.navercorp.pinpoint.profiler.context.grpc.GrpcAnnotationValueMapper;
import com.navercorp.pinpoint.profiler.context.grpc.config.SpanUriGetter;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Condition;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;

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
                AnnotationValueMapper.class,
                SpanUriGetter.class,
        }
)
public interface SpanMessageMapper {
    String DEFAULT_REMOTE_ADDRESS = "UNKNOWN";
    String DEFAULT_RPC_NAME = "UNKNOWN";
    String DEFAULT_END_POINT = "UNKNOWN";

    // WARNING: Thread unsafe
    GrpcAnnotationValueMapper grpcAnnotationValueMapper = new GrpcAnnotationValueMapper();


    @Mappings({
            @Mapping(source = "applicationServiceType", target = "version", qualifiedByName = "spanVersion"),
            @Mapping(source = "applicationServiceType", target = "applicationServiceType"),

            @Mapping(source = "span.traceRoot.traceId", target = "transactionId", qualifiedBy = TraceIdMapStructUtils.ToTransactionId.class),
            @Mapping(source = "span.traceRoot.traceId.spanId", target = "spanId"),
            @Mapping(source = "span.traceRoot.traceId.parentSpanId", target = "parentSpanId"),

            @Mapping(source = "span.elapsedTime", target = "elapsed"),

            @Mapping(source = "span", target = "acceptEvent", qualifiedByName = "toAcceptEvent"),

            @Mapping(source = "span.traceRoot.traceId.flags", target = "flag"),
            @Mapping(source = "span.traceRoot.shared.errorCode", target = "err"),

            @Mapping(source = "span.exceptionInfo", target = "exceptionInfo"),
            @Mapping(source = "span.traceRoot.shared.loggingInfo", target = "loggingTransactionInfo"),

            @Mapping(source = "span.annotations", target = "annotationList"),

            @Mapping(source = "span.spanEventList", target = "spanEventList")
    })
    void map(Span span, short applicationServiceType, @MappingTarget PSpan.Builder builder);

    @Mappings({
            @Mapping(source = "applicationServiceType", target = "version", qualifiedByName = "spanVersion"),
            @Mapping(source = "applicationServiceType", target = "applicationServiceType"),

            @Mapping(source = "spanChunk.traceRoot.traceId", target = "transactionId", qualifiedBy = TraceIdMapStructUtils.ToTransactionId.class),
            @Mapping(source = "spanChunk.traceRoot.traceId.spanId", target = "spanId"),
            @Mapping(source = "spanChunk.traceRoot.shared.endPoint", target = "endPoint"),

            @Mapping(source = "spanChunk.spanEventList", target = "spanEventList"),
    })
    void map(SpanChunk spanChunk, short applicationServiceType, @MappingTarget PSpanChunk.Builder builder);

    @InheritConfiguration
    @Mappings({
            @Mapping(source = "asyncSpanChunk.localAsyncId", target = "localAsyncId"),
    })
    void map(AsyncSpanChunk asyncSpanChunk, short applicationServiceType, @MappingTarget PSpanChunk.Builder builder);

    @Mappings({
    })
    PLocalAsyncId map(LocalAsyncId localAsyncId);


    @Named("spanVersion")
    default int spanVersion(short applicationServiceType) {
        return SpanVersion.TRACE_V2;
    }

    @Mappings({
            @Mapping(source = "elapsedTime", target = "endElapsed"),
            @Mapping(source = "depth", target = "depth"),
    })
    PSpanEvent map(SpanEvent spanEvent);

    @Mappings({
            @Mapping(source = ".", target = "value", qualifiedByName = "toAnnotationValue")
    })
    PAnnotation toProto(Annotation<?> annotation);

    @Named("toAnnotationValue")
    default PAnnotationValue toAnnotationValue(Annotation<?> annotation) {
        return grpcAnnotationValueMapper.buildPAnnotationValue(annotation);
    }

    @Named("toAcceptEvent")
    @Mappings({
            @Mapping(source = "remoteAddr", target = "remoteAddr", defaultValue = DEFAULT_REMOTE_ADDRESS),
            @Mapping(source = "traceRoot.shared", target = "rpc", qualifiedBy = SpanUriGetter.ToCollectedUri.class),
            @Mapping(source = "traceRoot.shared.endPoint", target = "endPoint", defaultValue = DEFAULT_END_POINT),

            @Mapping(source = ".", target = "parentInfo")
    })
    PAcceptEvent toAcceptEvent(Span span);

    @Mappings({
            @Mapping(source = "parentApplicationType", target = "parentApplicationType", conditionQualifiedByName = "isNotZero"),
    })
    PParentInfo toParentInfo(Span span);

    @Condition
    @Named("isNotZero")
    default boolean isNotZero(short parentApplicationType) {
        return parentApplicationType != 0;
    }

    @Condition
    default boolean isNotEmpty(String value) {
        return !StringUtils.isEmpty(value);
    }

}
