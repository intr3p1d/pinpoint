package com.navercorp.pinpoint.exceptiontrace.web;

import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * @author intr3p1d
 */
@PropertySources({
        @PropertySource(name = "ExceptionTracePropertySources", value = {ExceptionTraceWebPropertySources.EXCEPTION_TRACE}),
})
public class ExceptionTraceWebPropertySources {
    public static final String EXCEPTION_TRACE = "classpath:profiles/${pinpoint.profiles.active:release}/pinpoint-web-exceptiontrace.properties";
}
