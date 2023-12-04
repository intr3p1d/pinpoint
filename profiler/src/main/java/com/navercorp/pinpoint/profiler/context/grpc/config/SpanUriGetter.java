package com.navercorp.pinpoint.profiler.context.grpc.config;

import com.navercorp.pinpoint.profiler.context.id.Shared;
import org.mapstruct.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface SpanUriGetter {
    @Qualifier
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.CLASS)
    public @interface ToCollectedUri {
    }

    @ToCollectedUri
    String getCollectedUri(Shared shared);
}
