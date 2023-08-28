package com.navercorp.pinpoint.exceptiontrace.collector.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.pinpoint.common.server.util.json.Jackson;
import com.navercorp.pinpoint.common.util.StringUtils;
import com.navercorp.pinpoint.exceptiontrace.common.entity.ExceptionMetaDataEntity;
import com.navercorp.pinpoint.exceptiontrace.common.model.ExceptionMetaData;
import com.navercorp.pinpoint.exceptiontrace.common.model.StackTraceElementWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author intr3p1d
 */
class ModelToEntityMapperTest {

    private static final Logger logger = LogManager.getLogger(ModelToEntityMapperTest.class);
    private final Random random = new Random();
    private static final ObjectMapper objectMapper = Jackson.newMapper();

    ModelToEntityMapper modelToEntityMapper = new ModelToEntityMapper();

    @Test
    public void testModelToEntity() {
        Throwable throwable = new RuntimeException();

        ExceptionMetaData expected = newRandomExceptionMetaData(throwable);
        ExceptionMetaDataEntity actual = modelToEntityMapper.toEntity(expected);

        Assertions.assertEquals(expected.getTimestamp(), actual.getTimestamp());
        Assertions.assertEquals(expected.getTransactionId(), actual.getTransactionId());
        Assertions.assertEquals(expected.getSpanId(), actual.getSpanId());
        Assertions.assertEquals(expected.getExceptionId(), actual.getExceptionId());

        Assertions.assertEquals(expected.getApplicationServiceType(), actual.getApplicationServiceType());
        Assertions.assertEquals(expected.getApplicationName(), actual.getApplicationName());
        Assertions.assertEquals(expected.getAgentId(), actual.getAgentId());
        Assertions.assertEquals(expected.getUriTemplate(), actual.getUriTemplate());

        Assertions.assertEquals(expected.getErrorClassName(), actual.getErrorClassName());
        Assertions.assertEquals(expected.getErrorMessage(), actual.getErrorMessage());
        Assertions.assertEquals(expected.getExceptionDepth(), actual.getExceptionDepth());

        Assertions.assertEquals(expected.getStackTraceHash(), actual.getStackTraceHash());

        StackTraceElement[] expectedStackTrace = throwable.getStackTrace();
        int size = throwable.getStackTrace().length;

        String classNames = actual.getStackTraceClassName();
        String fileNames = actual.getStackTraceFileName();
        String lineNumbers = actual.getStackTraceLineNumber();
        String methodNames = actual.getStackTraceMethodName();

        List<String> classNameIter = convertToList(classNames);
        List<String> fileNameIter = convertToList(fileNames);
        List<Integer> lineNumberIter = convertToList(lineNumbers);
        List<String> methodNameIter = convertToList(methodNames);


        for (int i = 0; i < size; i++) {
            Assertions.assertEquals(expectedStackTrace[i].getClassName(), classNameIter.get(i));
            Assertions.assertEquals(expectedStackTrace[i].getFileName(), fileNameIter.get(i));
            Assertions.assertEquals(expectedStackTrace[i].getLineNumber(), lineNumberIter.get(i));
            Assertions.assertEquals(expectedStackTrace[i].getMethodName(), methodNameIter.get(i));
        }
    }


    private ExceptionMetaData newRandomExceptionMetaData(Throwable throwable) {
        List<StackTraceElementWrapper> wrapperList = wrapperList(throwable);

        return new ExceptionMetaData(
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
                wrapperList,
                "stackTraceHash"
        );
    }

    private List<StackTraceElementWrapper> wrapperList(Throwable throwable) {
        StackTraceElement[] stackTrace = throwable.getStackTrace();

        return Arrays.stream(stackTrace).map(
                (StackTraceElement s) -> new StackTraceElementWrapper(
                        s.getClassName(), s.getFileName(), s.getLineNumber(), s.getMethodName()
                )
        ).collect(Collectors.toList());
    }

    private  <T> List<T> convertToList(String s) {
        if (StringUtils.isEmpty(s)) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(s, new TypeReference<>() {
            });
        } catch (IOException e) {
            logger.error(e);
        }
        return Collections.emptyList();
    }
}