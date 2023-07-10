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
package com.navercorp.pinpoint.collector.handler.grpc;

import com.google.protobuf.GeneratedMessageV3;
import com.navercorp.pinpoint.collector.handler.RequestResponseHandler;
import com.navercorp.pinpoint.collector.service.ExceptionMetaDataService;
import com.navercorp.pinpoint.common.profiler.util.TransactionId;
import com.navercorp.pinpoint.common.server.bo.exception.ExceptionMetaDataBo;
import com.navercorp.pinpoint.common.server.bo.exception.ExceptionWrapperBo;
import com.navercorp.pinpoint.common.server.bo.exception.StackTraceElementWrapperBo;
import com.navercorp.pinpoint.common.util.StringUtils;
import com.navercorp.pinpoint.grpc.Header;
import com.navercorp.pinpoint.grpc.MessageFormatUtils;
import com.navercorp.pinpoint.grpc.server.ServerContext;
import com.navercorp.pinpoint.grpc.trace.PException;
import com.navercorp.pinpoint.grpc.trace.PExceptionMetaData;
import com.navercorp.pinpoint.grpc.trace.PResult;
import com.navercorp.pinpoint.grpc.trace.PStackTraceElement;
import com.navercorp.pinpoint.grpc.trace.PTransactionId;
import com.navercorp.pinpoint.io.request.ServerRequest;
import com.navercorp.pinpoint.io.request.ServerResponse;
import io.grpc.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author intr3p1d
 */
@Service
public class GrpcExceptionMetaDataHandler implements RequestResponseHandler<GeneratedMessageV3, GeneratedMessageV3> {

    private final Logger logger = LogManager.getLogger(getClass());
    private final boolean isDebug = logger.isDebugEnabled();

    private final ExceptionMetaDataService exceptionMetaDataService;

    public GrpcExceptionMetaDataHandler(ExceptionMetaDataService exceptionMetaDataService) {
        this.exceptionMetaDataService = Objects.requireNonNull(exceptionMetaDataService, "exceptionMetaDataService");
    }

    @Override
    public void handleRequest(ServerRequest<GeneratedMessageV3> serverRequest, ServerResponse<GeneratedMessageV3> serverResponse) {
        final GeneratedMessageV3 data = serverRequest.getData();
        if (data instanceof PExceptionMetaData) {
            PResult result = handleExceptionMetaData((PExceptionMetaData) data);
            serverResponse.write(result);
        } else {
            logger.warn("Invalid request type. serverRequest={}", serverRequest);
            throw Status.INTERNAL.withDescription("Bad Request(invalid request type)").asRuntimeException();
        }
    }

    private PResult handleExceptionMetaData(final PExceptionMetaData exceptionMetaData) {
        if (logger.isDebugEnabled()) {
            logger.debug("Handle PExceptionMetaData={}", MessageFormatUtils.debugLog(exceptionMetaData));
        }

        try {
            final Header agentInfo = ServerContext.getAgentInfo();
            final String agentId = agentInfo.getAgentId();
            final TransactionId transactionId = newTransactionId(exceptionMetaData.getTransactionId(), agentId);
            List<ExceptionWrapperBo> exceptionWrapperBos = new ArrayList<>();
            for (PException p : exceptionMetaData.getExceptionsList()) {
                exceptionWrapperBos.add(
                        new ExceptionWrapperBo(
                                p.getExceptionClassName(), p.getExceptionMessage(),
                                p.getStartTime(), p.getExceptionId(), p.getExceptionDepth(),
                                handleStackTraceElements(p.getStackTraceElementList())
                        )
                );
            }
            ExceptionMetaDataBo exceptionMetaDataBo = new ExceptionMetaDataBo(
                    transactionId, exceptionMetaData.getSpanId(),
                    (short) agentInfo.getServiceType(), agentInfo.getApplicationName(), agentInfo.getAgentId(),
                    exceptionMetaData.getUriTemplate(),
                    exceptionWrapperBos
            );
            exceptionMetaDataService.save(exceptionMetaDataBo);
            return PResult.newBuilder().setSuccess(true).build();
        } catch (Exception e) {
            logger.warn("Failed to handle exceptionMetaData={}", MessageFormatUtils.debugLog(exceptionMetaData), e);
            // Avoid detailed error messages.
            return PResult.newBuilder().setSuccess(false).setMessage("Internal Server Error").build();
        }
    }

    private List<StackTraceElementWrapperBo> handleStackTraceElements(List<PStackTraceElement> pStackTraceElements) {
        return pStackTraceElements.stream().map(
                (PStackTraceElement p) ->
                        new StackTraceElementWrapperBo(
                                p.getClassName(), p.getFileName(), p.getLineNumber(), p.getMethodName()
                        )
        ).collect(Collectors.toList());
    }

    private TransactionId newTransactionId(PTransactionId pTransactionId, String spanAgentId) {
        final String transactionAgentId = pTransactionId.getAgentId();
        if (StringUtils.hasLength(transactionAgentId)) {
            return new TransactionId(transactionAgentId, pTransactionId.getAgentStartTime(), pTransactionId.getSequence());
        } else {
            return new TransactionId(spanAgentId, pTransactionId.getAgentStartTime(), pTransactionId.getSequence());
        }
    }
}