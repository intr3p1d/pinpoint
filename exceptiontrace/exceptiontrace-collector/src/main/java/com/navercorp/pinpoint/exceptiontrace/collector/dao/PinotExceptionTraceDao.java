/*
 * Copyright 2023 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.exceptiontrace.collector.dao;

import com.navercorp.pinpoint.exceptiontrace.collector.model.ExceptionMetaDataVo;
import com.navercorp.pinpoint.exceptiontrace.common.model.ExceptionMetaData;
import com.navercorp.pinpoint.exceptiontrace.common.util.StringPrecondition;
import com.navercorp.pinpoint.pinot.kafka.util.KafkaCallbacks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Repository;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;
import java.util.Objects;

/**
 * @author intr3p1d
 */
@Repository
public class PinotExceptionTraceDao implements ExceptionTraceDao {
    private final Logger logger = LogManager.getLogger(this.getClass());

    private final KafkaTemplate<String, ExceptionMetaDataVo> kafkaExceptionMetaDataTemplate;

    private final String topic;

    private final ListenableFutureCallback<SendResult<String, ExceptionMetaDataVo>> resultCallback
            = KafkaCallbacks.loggingCallback("Kafka(ExceptionMetaDataVo)", logger);


    public PinotExceptionTraceDao(
            @Qualifier("kafkaExceptionMetaDataTemplate") KafkaTemplate<String, ExceptionMetaDataVo> kafkaExceptionMetaDataTemplate,
            @Value("${kafka.exception.topic}") String topic
    ) {
        this.kafkaExceptionMetaDataTemplate = Objects.requireNonNull(kafkaExceptionMetaDataTemplate, "kafkaExceptionMetaDataTemplate");
        this.topic = StringPrecondition.requireHasLength(topic, "topic");
    }

    @Override
    public void insert(List<ExceptionMetaData> exceptionMetaData) {
        Objects.requireNonNull(exceptionMetaData);
        logger.info("Pinot data insert: {}", exceptionMetaData.toString());

        for (ExceptionMetaData e : exceptionMetaData) {
            ListenableFuture<SendResult<String, ExceptionMetaDataVo>> response = this.kafkaExceptionMetaDataTemplate.send(
                    topic, ExceptionMetaDataVo.valueOf(e)
            );
            response.addCallback(resultCallback);
        }
    }
}
