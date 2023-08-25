package com.navercorp.pinpoint.exceptiontrace.collector.model;

import com.navercorp.pinpoint.exceptiontrace.common.model.StackTraceElementWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author intr3p1d
 */
class StackTraceElementWrappersListVoTest {

    @Test
    public void simpleEqualityTest() {
        // ignore
        Throwable throwable = new RuntimeException();
        StackTraceElement[] expected = throwable.getStackTrace();

        List<StackTraceElementWrapper> wrapperList = Arrays.stream(expected).map(
                (StackTraceElement s) -> new StackTraceElementWrapper(
                        s.getClassName(), s.getFileName(), s.getLineNumber(), s.getMethodName()
                )
        ).collect(Collectors.toList());

        StackTraceElementWrappersListVo actual = new StackTraceElementWrappersListVo(wrapperList);

        int size = throwable.getStackTrace().length;

        Iterator<String> classNameIter = actual.getStackTraceClassName().iterator();
        Iterator<String> fileNameIter = actual.getStackTraceFileName().iterator();
        Iterator<Integer> lineNumberIter = actual.getStackTraceLineNumber().iterator();
        Iterator<String> methodNameIter = actual.getStackTraceMethodName().iterator();

        for (int i = 0; i < size; i++) {
            Assertions.assertEquals(expected[i].getClassName(), classNameIter.next());
            Assertions.assertEquals(expected[i].getFileName(), fileNameIter.next());
            Assertions.assertEquals(expected[i].getLineNumber(), lineNumberIter.next());
            Assertions.assertEquals(expected[i].getMethodName(), methodNameIter.next());
        }

    }
}