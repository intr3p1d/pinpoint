package com.navercorp.pinpoint.exceptiontrace.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.pinpoint.common.server.util.json.Jackson;
import com.navercorp.pinpoint.exceptiontrace.common.model.ExceptionMetaData;
import com.navercorp.pinpoint.exceptiontrace.common.model.ExceptionMetaDataEntity;
import com.navercorp.pinpoint.exceptiontrace.common.model.StackTraceElementWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.navercorp.pinpoint.exceptiontrace.common.util.MapperUtils.convertToList;

/**
 * @author intr3p1d
 */
class MapperUtilsTest {

    private static final Logger logger = LogManager.getLogger(MapperUtilsTest.class);
    private final Random random = new Random();
    private static final ObjectMapper objectMapper = Jackson.newMapper();

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

    @Test
    public void testIdentityOfModel() {
        ModelMapper modelToEntityMapper = MapperUtils.newModelToEntityMapper();
        ModelMapper entityToModelMapper = MapperUtils.newEntityToModelMapper();
        Throwable throwable = new RuntimeException();
        List<StackTraceElementWrapper> wrapperList = wrapperList(throwable);

        ExceptionMetaData expected = newRandomExceptionMetaData(throwable);
        ExceptionMetaDataEntity intermediate = modelToEntityMapper.map(expected, ExceptionMetaDataEntity.class);
        ExceptionMetaData actual = entityToModelMapper.map(intermediate, ExceptionMetaData.class);

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


        for (int i = 0; i < actual.getStackTrace().size(); i++) {
            Assertions.assertEquals(expected.getStackTrace().get(i).getClassName(), actual.getStackTrace().get(i).getClassName());
            Assertions.assertEquals(expected.getStackTrace().get(i).getFileName(), actual.getStackTrace().get(i).getFileName());
            Assertions.assertEquals(expected.getStackTrace().get(i).getLineNumber(), actual.getStackTrace().get(i).getLineNumber());
            Assertions.assertEquals(expected.getStackTrace().get(i).getMethodName(), actual.getStackTrace().get(i).getMethodName());
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

        Assertions.assertEquals(expected.getStackTraceHash(), actual.getStackTraceHash());


        int size = throwable.getStackTrace().length;

        String classNames = expected.getStackTraceClassName();
        String fileNames = expected.getStackTraceFileName();
        String lineNumbers = expected.getStackTraceLineNumber();
        String methodNames = expected.getStackTraceMethodName();

        List<String> classNameIter = convertToList(classNames);
        List<String> fileNameIter = convertToList(fileNames);
        List<Integer> lineNumberIter = convertToList(lineNumbers);
        List<String> methodNameIter = convertToList(methodNames);

        List<StackTraceElementWrapper> actualStackTrace = actual.getStackTrace();

        for (int i = 0; i < size; i++) {
            Assertions.assertEquals(classNameIter.get(i), actualStackTrace.get(i).getClassName());
            Assertions.assertEquals(fileNameIter.get(i), actualStackTrace.get(i).getFileName());
            Assertions.assertEquals(lineNumberIter.get(i), actualStackTrace.get(i).getLineNumber());
            Assertions.assertEquals(methodNameIter.get(i), actualStackTrace.get(i).getMethodName());
        }
    }

    @Test
    public void testIdentityOfEntity() {
        ModelMapper modelToEntityMapper = MapperUtils.newModelToEntityMapper();
        ModelMapper entityToModelMapper = MapperUtils.newEntityToModelMapper();
        Throwable throwable = new RuntimeException();

        ExceptionMetaDataEntity expected = newExceptionMetaDataEntity(throwable);
        ExceptionMetaData intermediate = entityToModelMapper.map(expected, ExceptionMetaData.class);
        ExceptionMetaDataEntity actual = modelToEntityMapper.map(intermediate, ExceptionMetaDataEntity.class);

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

        Assertions.assertEquals(expected.getStackTraceClassName(), actual.getStackTraceClassName());
        Assertions.assertEquals(expected.getStackTraceFileName(), actual.getStackTraceFileName());
        Assertions.assertEquals(expected.getStackTraceLineNumber(), actual.getStackTraceLineNumber());
        Assertions.assertEquals(expected.getStackTraceMethodName(), actual.getStackTraceMethodName());
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

        dataEntity.setStackTraceClassName(toFlattenedString(elements, StackTraceElement::getClassName));
        dataEntity.setStackTraceFileName(toFlattenedString(elements, StackTraceElement::getFileName));
        dataEntity.setStackTraceLineNumber(toFlattenedString(elements, StackTraceElement::getLineNumber));
        dataEntity.setStackTraceMethodName(toFlattenedString(elements, StackTraceElement::getMethodName));
        return dataEntity;
    }

    private <T> String toFlattenedString(List<StackTraceElement> elements, Function<StackTraceElement, T> getter) {
        try {
            return objectMapper.writeValueAsString(elements.stream().map(getter).collect(Collectors.toList()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}