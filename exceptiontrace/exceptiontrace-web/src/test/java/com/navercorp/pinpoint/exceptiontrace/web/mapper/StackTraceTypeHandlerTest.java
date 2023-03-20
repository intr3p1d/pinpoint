package com.navercorp.pinpoint.exceptiontrace.web.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.pinpoint.exceptiontrace.common.model.StackTraceElementWrapper;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * @author intr3p1d
 */
public class StackTraceTypeHandlerTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final static StackTraceTypeHandler stackTraceTypeHandler = new StackTraceTypeHandler();

    private List<StackTraceElementWrapper> newStackTraceElementWrappers(Throwable throwable) {
        List<StackTraceElement> elements = List.of(throwable.getStackTrace());
        return elements.stream().map(
                (StackTraceElement s) -> new StackTraceElementWrapper(
                        s.getClassName(),
                        s.getFileName(),
                        s.getLineNumber(),
                        s.getMethodName()
                )
        ).collect(Collectors.toList());
    }

    private String serializedStackTraceElementWrappers(List<StackTraceElementWrapper> wrappers) throws JsonProcessingException {
        List<String> strings = new ArrayList<>();
        wrappers.forEach(
                (StackTraceElementWrapper s) -> {
                    try {
                        strings.add(OBJECT_MAPPER.writeValueAsString(s));
                    } catch (JsonProcessingException ignored) {
                        // do nothing
                    }
                }
        );
        return OBJECT_MAPPER.writeValueAsString(strings);
    }

    @Test
    public void testConvertToList() throws JsonProcessingException {
        Throwable exception = new RuntimeException("sample exception");
        List<StackTraceElementWrapper> expected = newStackTraceElementWrappers(exception);
        List<StackTraceElementWrapper> actual = stackTraceTypeHandler.convertToList(serializedStackTraceElementWrappers(expected));
        assertThat(actual).isEqualTo(expected);
    }

}
