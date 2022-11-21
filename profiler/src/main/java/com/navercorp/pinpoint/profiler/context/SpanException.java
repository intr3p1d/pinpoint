package com.navercorp.pinpoint.profiler.context;

import java.util.Objects;

/**
 * @author intr3p1d
 */
public class SpanException {

    private final int depth;
    private final long refId;

    private final Throwable throwable; // optional, not null when top level exception

    public SpanException(int depth, Throwable throwable, long refId) {
        this.depth = Objects.requireNonNull(depth, "depth");
        this.refId = Objects.requireNonNull(refId, "refId");
        this.throwable = throwable;
    }
}
