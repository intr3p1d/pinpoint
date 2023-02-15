package com.navercorp.pinpoint.exceptiontrace.web.mapper;

import com.navercorp.pinpoint.exceptiontrace.common.model.StackTraceElementWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author intr3p1d
 */
public class StackTraceTypeHandlerTest {

    private final Logger logger = LogManager.getLogger(this.getClass());
    private final String string = "[{\"className\":\"org.springframework.web.servlet.FrameworkServlet\",\"fileName\":\"FrameworkServlet.java\",\"lineNumber\":1014,\"methodName\":\"processRequest\"}]";

    private final StackTraceTypeHandler stackTraceTypeHandler = new StackTraceTypeHandler();

    @Test
    public void testConvertToList(){
        List<StackTraceElementWrapper> stackTraceElementWrapperList = stackTraceTypeHandler.convertToList(string);
        logger.error(stackTraceElementWrapperList);
    }

}
