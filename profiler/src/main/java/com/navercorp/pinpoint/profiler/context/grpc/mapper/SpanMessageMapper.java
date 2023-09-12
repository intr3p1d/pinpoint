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
import com.navercorp.pinpoint.grpc.trace.PAnnotationValue;
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
import com.navercorp.pinpoint.profiler.context.module.ApplicationServerType;
import com.navercorp.pinpoint.profiler.context.provider.AgentInformationProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeforeMapping;
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

import java.util.Comparator;
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

    static final byte V2 = TraceDataFormatVersion.V2.getVersion();
    final static Comparator<SpanEvent> SEQUENCE_COMPARATOR = SpanEventSequenceComparator.INSTANCE;

    public static final String DEFAULT_END_POINT = "UNKNOWN";
    public static final String DEFAULT_RPC_NAME = "UNKNOWN";
    public static final String DEFAULT_REMOTE_ADDRESS = "UNKNOWN";

    // WARNING: Thread unsafe
    GrpcAnnotationValueMapper grpcAnnotationValueMapper = new GrpcAnnotationValueMapper();


    @Mappings({
            @Mapping(source = ".", target = "version", qualifiedByName = "currentVersion"),

            @Mapping(source = "span.traceRoot.traceId", target = "transactionId", qualifiedBy = TraceIdMapStructUtils.ToTransactionId.class),
            @Mapping(source = "span.traceRoot.traceId.spanId", target = "spanId"),
            @Mapping(source = "span.traceRoot.traceId.parentSpanId", target = "parentSpanId"),

            @Mapping(source = "span.elapsedTime", target = "elapsed"),

            @Mapping(source = ".", target = "acceptEvent", qualifiedByName = "toAcceptEvent"),

            @Mapping(source = "span.traceRoot.traceId.flags", target = "flag"),
            @Mapping(source = "span.traceRoot.shared.errorCode", target = "err"),

            @Mapping(source = "span.traceRoot.shared.loggingInfo", target = "loggingTransactionInfo"),

            @Mapping(source = "span.annotations", target = "annotationList"),

            @Mapping(source = "span.spanEventList", target = "spanEventList")
    })
    public abstract void toProto(Span span, @MappingTarget PSpan.Builder builder);

    @Named("currentVersion")
    default int currentVersion(Span span){
        return SpanVersion.TRACE_V2;
    }

    @Mappings({
            @Mapping(source = "elapsedTime", target = "endElapsed"),
            @Mapping(source = "depth", target = "depth"),
    })
    public abstract PSpanEvent toProto(SpanEvent spanEvent);

    @Mappings({
            @Mapping(source = ".", target = "value", qualifiedByName = "toAnnotationValue")
    })
    public abstract PAnnotation toProto(Annotation<?> annotation);

    @Named("toAnnotationValue")
    default PAnnotationValue toAnnotationValue(Annotation<?> annotation) {
        return grpcAnnotationValueMapper.buildPAnnotationValue(annotation);
    }

    @Named("toAcceptEvent")
    @Mappings({
            @Mapping(source = "traceRoot.shared", target = "rpc", qualifiedBy = SpanUriGetter.ToCollectedUri.class),
            @Mapping(source = "traceRoot.shared.endPoint", target = "endPoint"),

            @Mapping(source = ".", target = "parentInfo", qualifiedByName = "toParentInfo")
    })
    public abstract PAcceptEvent toAcceptEvent(Span span);

    @Named("toParentInfo")
    @Mappings({
    })
    PParentInfo toParentInfo(Span span);

    @Condition
    default boolean isNotEmpty(String value) {
        return !StringUtils.isEmpty(value);
    }

}
