package com.navercorp.pinpoint.profiler.context;

import java.util.Objects;

/**
 * @author intr3p1d
 */
public class SpanException {

    private String exceptionClassName;
    private String exceptionMessage;

    private final long refId;

    private final Throwable throwable; // optional, not null when top level exception

    public SpanException(Throwable throwable, long refId) {
        this.refId = Objects.requireNonNull(refId, "refId");
        this.throwable = throwable;
    }
}
