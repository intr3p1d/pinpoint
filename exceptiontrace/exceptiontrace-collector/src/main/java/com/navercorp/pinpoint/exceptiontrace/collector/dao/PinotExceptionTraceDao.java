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

import com.navercorp.pinpoint.common.server.util.StringPrecondition;
import com.navercorp.pinpoint.exceptiontrace.collector.mapper.ModelToEntityMapper;
import com.navercorp.pinpoint.exceptiontrace.common.model.ExceptionMetaData;
import com.navercorp.pinpoint.exceptiontrace.common.entity.ExceptionMetaDataEntity;
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

    private final KafkaTemplate<String, ExceptionMetaDataEntity> kafkaExceptionMetaDataTemplate;

    private final ModelToEntityMapper modelToEntityMapper;

    private final String topic;

    private final ListenableFutureCallback<SendResult<String, ExceptionMetaDataEntity>> resultCallback
            = KafkaCallbacks.loggingCallback("Kafka(ExceptionMetaDataEntity)", logger);


    public PinotExceptionTraceDao(
            @Qualifier("kafkaExceptionMetaDataTemplate") KafkaTemplate<String, ExceptionMetaDataEntity> kafkaExceptionMetaDataTemplate,
            @Value("${kafka.exception.topic}") String topic,
            ModelToEntityMapper modelToEntityMapper
    ) {
        this.kafkaExceptionMetaDataTemplate = Objects.requireNonNull(kafkaExceptionMetaDataTemplate, "kafkaExceptionMetaDataTemplate");
        this.topic = StringPrecondition.requireHasLength(topic, "topic");
        this.modelToEntityMapper = Objects.requireNonNull(modelToEntityMapper, "modelToEntityMapper");
    }

    @Override
    public void insert(List<ExceptionMetaData> exceptionMetaData) {
        Objects.requireNonNull(exceptionMetaData);
        logger.info("Pinot data insert: {}", exceptionMetaData);

        for (ExceptionMetaData e : exceptionMetaData) {
            ExceptionMetaDataEntity dataEntity = modelToEntityMapper.toEntity(e);
            ListenableFuture<SendResult<String, ExceptionMetaDataEntity>> response = this.kafkaExceptionMetaDataTemplate.send(
                    topic, dataEntity
            );
            response.addCallback(resultCallback);
        }
    }
}
