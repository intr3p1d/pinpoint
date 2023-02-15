package com.navercorp.pinpoint.exceptiontrace.collector.dao;

import com.navercorp.pinpoint.exceptiontrace.common.model.SpanEventException;
import com.navercorp.pinpoint.exceptiontrace.common.util.StringPrecondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

/**
 * @author intr3p1d
 */
@Repository
public class PinotExceptionTraceDao implements ExceptionTraceDao {
    private final Logger logger = LogManager.getLogger(this.getClass());

    private final KafkaTemplate<String, SpanEventExceptionVo> kafkaSpanEventExceptionTemplate;

    private final String topic;

    public PinotExceptionTraceDao(@Qualifier("kafkaSpanEventExceptionTemplate") KafkaTemplate<String, SpanEventExceptionVo> kafkaSpanEventExceptionTemplate,
                                  @Value("${kafka.exceptiontrace.topic}") String topic) {
        this.kafkaSpanEventExceptionTemplate = Objects.requireNonNull(kafkaSpanEventExceptionTemplate, "kafkaSpanEventExceptionTemplate");
        this.topic = StringPrecondition.requireHasLength(topic, "topic");
    }

    @Override
    public void insert(List<SpanEventException> spanEventExceptions) {
        logger.warn("Pinot data insert: ExceptionTraceDao called");
        Objects.requireNonNull(spanEventExceptions);

        for (SpanEventException spanEventException : spanEventExceptions) {
            logger.warn(spanEventException.toString());
            this.kafkaSpanEventExceptionTemplate.send(topic, SpanEventExceptionVo.toSpanEventExceptionVo(spanEventException));
        }
    }
}
