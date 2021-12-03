/*
 * Copyright 2021 NAVER Corp.
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

package com.navercorp.pinpoint.web.config;

import com.navercorp.pinpoint.common.server.config.AnnotationVisitor;
import com.navercorp.pinpoint.common.server.config.LoggingEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class ExperimentalConfig {

    private final Logger logger = LoggerFactory.getLogger(ConfigProperties.class);

    @Value("false")
    private boolean enableServerSideScanForScatter;

    @Value("false")
    private boolean useStatisticsAgentState;

    @Value("true")
    private boolean enableServerMapRealTime;

    @Value("true")
    private boolean sampleScatter;

    public boolean isEnableServerSideScanForScatter() { return enableServerSideScanForScatter; }

    public boolean isUseStatisticsAgentState() { return useStatisticsAgentState; }

    public boolean isEnableServerMapRealTime() {
        return enableServerMapRealTime;
    }

    public boolean isSampleScatter() { return sampleScatter; }

    @PostConstruct
    public void log() {
        logger.info("{}", this);
        AnnotationVisitor<Value> annotationVisitor = new AnnotationVisitor<>(Value.class);
        annotationVisitor.visit(this, new LoggingEvent(this.logger));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ExperimentalConfig{");
        sb.append(", enableServerSideScanForScatter=").append(enableServerSideScanForScatter);
        sb.append(", useStatisticsAgentState=").append(useStatisticsAgentState);
        sb.append(", enableServerMapRealTime=").append(enableServerMapRealTime);
        sb.append(", sampleScatter=").append(sampleScatter);
        sb.append('}');
        return sb.toString();
    }

    public ExperimentalConfig(){
        enableServerSideScanForScatter = false;
        useStatisticsAgentState = false;
        enableServerMapRealTime = true;
        sampleScatter = true;
    }
}
