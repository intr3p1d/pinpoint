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
import com.navercorp.pinpoint.profiler.context.Span;
import com.navercorp.pinpoint.profiler.context.compress.SpanProcessor;
import com.navercorp.pinpoint.profiler.context.grpc.GrpcAnnotationValueMapper;
import com.navercorp.pinpoint.profiler.context.grpc.config.SpanUriGetter;
import com.navercorp.pinpoint.profiler.context.id.Shared;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Condition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.Objects;

/**
 * @author intr3p1d
 */
@Mapper(componentModel = MappingConstants.ComponentModel.JSR330,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
        nullValueCheckStrategy = NullValueCheckStrategy.ON_IMPLICIT_CONVERSION,
        uses = {

        }
)
public interface SpanMessageMapper {

    public static final String DEFAULT_END_POINT = "UNKNOWN";
    public static final String DEFAULT_RPC_NAME = "UNKNOWN";
    public static final String DEFAULT_REMOTE_ADDRESS = "UNKNOWN";


    @Mappings({
            @Mapping(source = "traceRoot.traceId", target = "transactionId", qualifiedBy = TraceIdMapStructUtils.ToTransactionId.class),
            @Mapping(source = "traceRoot.traceId.spanId", target = "spanId"),
            @Mapping(source = "traceRoot.traceId.parentSpanId", target = "parentSpanId"),

            // PAcceptEvent
            @Mapping(source = "remoteAddr", target = "pAcceptEvent.remoteAddr", defaultValue = DEFAULT_REMOTE_ADDRESS),
            @Mapping(source = "traceRoot.shared", target = "pAcceptEvent.rpc", qualifiedByName = "toRPCName", defaultValue = DEFAULT_RPC_NAME),
            @Mapping(source = "traceRoot.shared.endPoint", target = "pAcceptEvent.endPoint", defaultValue = DEFAULT_END_POINT),

            // PAcceptEvent PParentInfo
            @Mapping(source = "parentApplicationName", target = "pAcceptEvent.pParentInfo.parentApplicationName"),
            @Mapping(source = "parentApplicationType", target = "pAcceptEvent.pParentInfo.parentApplicationType"),
            @Mapping(source = "accecptorHost", target = "pAcceptEvent.pParentInfo.acceptorHost"),

            @Mapping(source = "traceRoot.flags", target = "flag"),
            @Mapping(source = "traceRoot.shared.errorCode", target = "err"),

            @Mapping(source = "traceRoot.shared.loggingInfo", target = "loggingTransactionInfo"),


    })
    PSpan toProto(Span span);

    PIntStringValue toProto(IntStringValue intStringValue);

    @Condition
    default boolean isNotEmpty(String value) {
        return !StringUtils.isEmpty(value);
    }

    @Named("toRPCName")
    default String toRPCName(Shared shared) {
        return "";
    }

}
