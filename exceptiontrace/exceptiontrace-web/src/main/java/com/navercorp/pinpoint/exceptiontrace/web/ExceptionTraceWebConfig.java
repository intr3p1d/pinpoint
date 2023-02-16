package com.navercorp.pinpoint.exceptiontrace.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;

/**
 * @author intr3p1d
 */
@Configuration
@ImportResource({"classpath:applicationContext-web-exceptiontrace.xml"})
@Import(ExceptionTraceWebPropertySources.class)
@Profile("metric")
public class ExceptionTraceWebConfig {
}
