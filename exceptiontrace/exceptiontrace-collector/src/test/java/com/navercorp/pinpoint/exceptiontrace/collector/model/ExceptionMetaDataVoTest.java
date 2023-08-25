package com.navercorp.pinpoint.exceptiontrace.collector.model;


import com.navercorp.pinpoint.exceptiontrace.common.model.ExceptionMetaData;
import com.navercorp.pinpoint.exceptiontrace.common.model.StackTraceWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Random;

/**
 * @author intr3p1d
 */
class ExceptionMetaDataVoTest {

    private final Random random = new Random();

    @Test
    public void simpleEqualityTest() {
        ExceptionMetaData exceptionMetaData = new ExceptionMetaData(
                random.nextLong(),
                "transactionId",
                random.nextLong(),
                random.nextLong(),
                "applicationServiceType",
                "applicationName",
                "agentId",
                "uriTemplate",
                "errorClassName",
                "errorMessage",
                random.nextInt(),
                new StackTraceWrapper(Collections.emptyList()),
                "stackTraceHash"
        );
        ExceptionMetaDataVo exceptionMetaDataVo = ExceptionMetaDataVo.valueOf(exceptionMetaData);

        Assertions.assertEquals(exceptionMetaData.getTimestamp(), exceptionMetaDataVo.getTimestamp());
        Assertions.assertEquals(exceptionMetaData.getTransactionId(), exceptionMetaDataVo.getTransactionId());
        Assertions.assertEquals(exceptionMetaData.getSpanId(), exceptionMetaDataVo.getSpanId());
        Assertions.assertEquals(exceptionMetaData.getExceptionId(), exceptionMetaDataVo.getExceptionId());

        Assertions.assertEquals(exceptionMetaData.getApplicationServiceType(), exceptionMetaDataVo.getApplicationServiceType());
        Assertions.assertEquals(exceptionMetaData.getApplicationName(), exceptionMetaDataVo.getApplicationName());
        Assertions.assertEquals(exceptionMetaData.getAgentId(), exceptionMetaDataVo.getAgentId());
        Assertions.assertEquals(exceptionMetaData.getUriTemplate(), exceptionMetaDataVo.getUriTemplate());

        Assertions.assertEquals(exceptionMetaData.getErrorClassName(), exceptionMetaDataVo.getErrorClassName());
        Assertions.assertEquals(exceptionMetaData.getErrorMessage(), exceptionMetaDataVo.getErrorMessage());
        Assertions.assertEquals(exceptionMetaData.getExceptionDepth(), exceptionMetaDataVo.getExceptionDepth());

    }
}