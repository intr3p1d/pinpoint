package com.navercorp.pinpoint.exceptiontrace.web.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.pinpoint.common.server.util.json.Jackson;
import com.navercorp.pinpoint.exceptiontrace.common.model.ExceptionMetaDataEntity;
import com.navercorp.pinpoint.exceptiontrace.common.model.ExceptionTraceValueViewEntity;
import com.navercorp.pinpoint.exceptiontrace.web.model.ExceptionTraceValueView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author intr3p1d
 */
class ValueViewMapperUtilsTest {

    private static final Logger logger = LogManager.getLogger(ValueViewMapperUtilsTest.class);
    private final Random random = new Random();
    private static final ObjectMapper objectMapper = Jackson.newMapper();

    @Test
    public void testEntityToValueView() {
        ModelMapper modelMapper = ValueViewMapperUtils.newValueViewModelMapper();
        ExceptionTraceValueViewEntity expected = newExceptionMetaDataEntity();

        ExceptionTraceValueView actual = modelMapper.map(expected, ExceptionTraceValueView.class);

        Assertions.assertEquals(expected.getUriTemplate(), actual.getGroupedFieldName().getUriTemplate());
        Assertions.assertEquals(expected.getErrorClassName(), actual.getGroupedFieldName().getErrorClassName());
        Assertions.assertEquals(expected.getErrorMessage(), actual.getGroupedFieldName().getErrorMessage());
        Assertions.assertEquals(expected.getStackTraceHash(), actual.getGroupedFieldName().getStackTraceHash());

        Assertions.assertNotNull(actual.getValues());
        Assertions.assertFalse(actual.getValues().isEmpty());
    }


    private ExceptionTraceValueViewEntity newExceptionMetaDataEntity() {
        ExceptionTraceValueViewEntity dataEntity = new ExceptionTraceValueViewEntity();

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

        dataEntity.setValues("[0,83,2,12]");
        return dataEntity;
    }

}