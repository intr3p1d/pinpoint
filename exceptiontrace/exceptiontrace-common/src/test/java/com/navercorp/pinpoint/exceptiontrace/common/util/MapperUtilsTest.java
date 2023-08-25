package com.navercorp.pinpoint.exceptiontrace.common.util;

import com.navercorp.pinpoint.exceptiontrace.common.model.ExceptionMetaData;
import com.navercorp.pinpoint.exceptiontrace.common.model.ExceptionMetaDataEntity;
import com.navercorp.pinpoint.exceptiontrace.common.model.StackTraceElementWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author intr3p1d
 */
class MapperUtilsTest {

    private final Random random = new Random();

    @Test
    public void testModelToEntity() {
        ModelMapper modelMapper = MapperUtils.newModelToEntityMapper();
        Throwable throwable = new RuntimeException();
        List<StackTraceElementWrapper> wrapperList = wrapperList(throwable);

        ExceptionMetaData expected = newRandomExceptionMetaData(throwable);
        ExceptionMetaDataEntity actual = modelMapper.map(expected, ExceptionMetaDataEntity.class);

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

        StackTraceElement[] expectedStackTrace = throwable.getStackTrace();
        int size = throwable.getStackTrace().length;

        List<String> classNameIter = actual.getStackTraceClassName();
        List<String> fileNameIter = actual.getStackTraceFileName();
        List<Integer> lineNumberIter = actual.getStackTraceLineNumber();
        List<String> methodNameIter = actual.getStackTraceMethodName();

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

    @Test
    public void testEntityToModel() {
        ModelMapper modelMapper = MapperUtils.newEntityToModelMapper();
        Throwable throwable = new RuntimeException();

        ExceptionMetaDataEntity expected = newExceptionMetaDataEntity(throwable);
        ExceptionMetaData actual = modelMapper.map(expected, ExceptionMetaData.class);

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

        int size = throwable.getStackTrace().length;

        List<String> classNameIter = expected.getStackTraceClassName();
        List<String> fileNameIter = expected.getStackTraceFileName();
        List<Integer> lineNumberIter = expected.getStackTraceLineNumber();
        List<String> methodNameIter = expected.getStackTraceMethodName();

        List<StackTraceElementWrapper> actualStackTrace = actual.getStackTrace();

        for (int i = 0; i < size; i++) {
            Assertions.assertEquals(classNameIter.get(i), actualStackTrace.get(i).getClassName());
            Assertions.assertEquals(fileNameIter.get(i), actualStackTrace.get(i).getFileName());
            Assertions.assertEquals(lineNumberIter.get(i), actualStackTrace.get(i).getLineNumber());
            Assertions.assertEquals(methodNameIter.get(i), actualStackTrace.get(i).getMethodName());
        }

    }

    private ExceptionMetaDataEntity newExceptionMetaDataEntity(Throwable throwable) {
        ExceptionMetaDataEntity dataEntity = new ExceptionMetaDataEntity();

        dataEntity.setTimestamp(random.nextLong());
        dataEntity.setTransactionId("transactionId");
        dataEntity.setSpanId(random.nextLong());
        dataEntity.setExceptionId(random.nextLong());
        dataEntity.setApplicationServiceType("applicationServiceType");
        dataEntity.setApplicationName("applicationName");
        dataEntity.setAgentId("agentId");
        dataEntity.setUriTemplate("uriTemplate");
        dataEntity.setErrorClassName("errorClassName");
        dataEntity.setErrorMessage("errorMessage");
        dataEntity.setExceptionDepth(random.nextInt());
        dataEntity.setStackTraceHash("stackTraceHash");

        List<StackTraceElement> elements = List.of(throwable.getStackTrace());

        dataEntity.setStackTraceClassName(elements.stream().map(StackTraceElement::getClassName).collect(Collectors.toList()));
        dataEntity.setStackTraceFileName(elements.stream().map(StackTraceElement::getFileName).collect(Collectors.toList()));
        dataEntity.setStackTraceLineNumber(elements.stream().map(StackTraceElement::getLineNumber).collect(Collectors.toList()));
        dataEntity.setStackTraceMethodName(elements.stream().map(StackTraceElement::getMethodName).collect(Collectors.toList()));
        return dataEntity;
    }
}