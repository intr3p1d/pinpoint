package com.navercorp.pinpoint.metric.collector.dao.pinot;

import com.navercorp.pinpoint.common.server.bo.exception.SpanEventExceptionBo;
import com.navercorp.pinpoint.metric.collector.dao.ExceptionTraceDao;
import com.navercorp.pinpoint.metric.common.model.SpanEventException;
import com.navercorp.pinpoint.metric.common.model.StringPrecondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;

import java.util.Objects;

/**
 * @author intr3p1d
 */
@Repository
public class PinotExceptionTraceDao implements ExceptionTraceDao {
    private final Logger logger = LogManager.getLogger(this.getClass());

    private final KafkaTemplate<String, SpanEventException> kafkaSpanEventExceptionTemplate;

    private final String topic;

    public PinotExceptionTraceDao(KafkaTemplate<String, SpanEventException> kafkaSpanEventExceptionTemplate,
                                  @Value("${kafka.exceptiontrace.topic}") String topic) {
        this.kafkaSpanEventExceptionTemplate = Objects.requireNonNull(kafkaSpanEventExceptionTemplate, "kafkaSpanEventExceptionTemplate");
        this.topic = StringPrecondition.requireHasLength(topic, "topic");
    }

    @Override
    public void insert(SpanEventExceptionBo spanEventExceptionBo) {
        logger.warn("Pinot data insert: ExceptionTraceDao called");
        logger.debug(spanEventExceptionBo);
        logger.warn("startTime: {}", spanEventExceptionBo.getStartTime());
    }
}
