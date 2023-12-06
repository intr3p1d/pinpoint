/*
 * Copyright 2019 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.profiler.context.grpc;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.StringValue;
import com.navercorp.pinpoint.bootstrap.context.TraceId;
import com.navercorp.pinpoint.common.annotations.VisibleForTesting;
import com.navercorp.pinpoint.common.profiler.logging.ThrottledLogger;
import com.navercorp.pinpoint.common.profiler.message.MessageConverter;
import com.navercorp.pinpoint.common.util.CollectionUtils;
import com.navercorp.pinpoint.common.util.IntStringValue;
import com.navercorp.pinpoint.common.util.StringUtils;
import com.navercorp.pinpoint.grpc.trace.PAcceptEvent;
import com.navercorp.pinpoint.grpc.trace.PAnnotation;
import com.navercorp.pinpoint.grpc.trace.PAnnotationValue;
import com.navercorp.pinpoint.grpc.trace.PIntStringValue;
import com.navercorp.pinpoint.grpc.trace.PLocalAsyncId;
import com.navercorp.pinpoint.grpc.trace.PMessageEvent;
import com.navercorp.pinpoint.grpc.trace.PNextEvent;
import com.navercorp.pinpoint.grpc.trace.PParentInfo;
import com.navercorp.pinpoint.grpc.trace.PSpan;
import com.navercorp.pinpoint.grpc.trace.PSpanChunk;
import com.navercorp.pinpoint.grpc.trace.PSpanEvent;
import com.navercorp.pinpoint.grpc.trace.PTransactionId;
import com.navercorp.pinpoint.io.SpanVersion;
import com.navercorp.pinpoint.profiler.context.Annotation;
import com.navercorp.pinpoint.profiler.context.AsyncId;
import com.navercorp.pinpoint.profiler.context.AsyncSpanChunk;
import com.navercorp.pinpoint.profiler.context.LocalAsyncId;
import com.navercorp.pinpoint.profiler.context.Span;
import com.navercorp.pinpoint.profiler.context.SpanChunk;
import com.navercorp.pinpoint.profiler.context.SpanEvent;
import com.navercorp.pinpoint.profiler.context.SpanType;
import com.navercorp.pinpoint.profiler.context.compress.SpanProcessor;
import com.navercorp.pinpoint.profiler.context.grpc.config.SpanUriGetter;
import com.navercorp.pinpoint.profiler.context.grpc.mapper.SpanMessageMapper;
import com.navercorp.pinpoint.profiler.context.id.Shared;
import com.navercorp.pinpoint.profiler.context.id.TraceRoot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Not thread safe
 *
 * @author Woonduk Kang(emeroad)
 */
public class GrpcSpanMessageConverter implements MessageConverter<SpanType, GeneratedMessageV3> {
    public static final String DEFAULT_END_POINT = "UNKNOWN";
    public static final String DEFAULT_RPC_NAME = "UNKNOWN";
    public static final String DEFAULT_REMOTE_ADDRESS = "UNKNOWN";

    private final Logger logger = LogManager.getLogger(this.getClass());
    private final ThrottledLogger throttledLogger = ThrottledLogger.getLogger(this.logger, 100);

    private final String agentId;
    private final short applicationServiceType;

    private final SpanProcessor<PSpan.Builder, PSpanChunk.Builder> spanProcessor;
    // WARNING not thread safe
    private final GrpcAnnotationValueMapper grpcAnnotationValueMapper = new GrpcAnnotationValueMapper();

    private final PSpanEvent.Builder pSpanEventBuilder = PSpanEvent.newBuilder();
    private final PAnnotation.Builder pAnnotationBuilder = PAnnotation.newBuilder();

    private final SpanUriGetter spanUriGetter;

    private final SpanMessageMapper mapper;

    public GrpcSpanMessageConverter(String agentId, short applicationServiceType,
                                    SpanProcessor<PSpan.Builder, PSpanChunk.Builder> spanProcessor,
                                    SpanUriGetter spanUriGetter,
                                    SpanMessageMapper spanMessageMapper) {
        this.agentId = Objects.requireNonNull(agentId, "agentId");
        this.applicationServiceType = applicationServiceType;
        this.spanProcessor = Objects.requireNonNull(spanProcessor, "spanProcessor");
        this.spanUriGetter = Objects.requireNonNull(spanUriGetter);
        this.mapper = Objects.requireNonNull(spanMessageMapper, "spanMessageMapper");
    }

    @Override
    public GeneratedMessageV3 toMessage(SpanType message) {
        if (message instanceof SpanChunk) {
            final SpanChunk spanChunk = (SpanChunk) message;
            return buildPSpanChunk(spanChunk);
        }
        if (message instanceof Span) {
            final Span span = (Span) message;
            return buildPSpan(span);
        }
        return null;
    }

    @VisibleForTesting
    PSpan buildPSpan(Span span) {
        final PSpan.Builder pSpan = PSpan.newBuilder();

        this.spanProcessor.preProcess(span, pSpan);
        mapper.map(span, applicationServiceType, pSpan);
        this.spanProcessor.postProcess(span, pSpan);
        return pSpan.build();

    }

    @VisibleForTesting
    PSpanChunk buildPSpanChunk(SpanChunk spanChunk) {
        final PSpanChunk.Builder pSpanChunk = PSpanChunk.newBuilder();

        this.spanProcessor.preProcess(spanChunk, pSpanChunk);
        mapper.map(spanChunk, applicationServiceType, pSpanChunk);
        this.spanProcessor.postProcess(spanChunk, pSpanChunk);
        return pSpanChunk.build();
    }

    @Override
    public String toString() {
        return "GrpcSpanMessageConverter{" +
                "agentId='" + agentId + '\'' +
                ", applicationServiceType=" + applicationServiceType +
                ", spanProcessor=" + spanProcessor +
                '}';
    }
}
