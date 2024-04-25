/*
 * Copyright 2024 NAVER Corp.
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
package com.navercorp.pinpoint.web.vo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.pinpoint.common.server.util.json.Jackson;
import org.springframework.core.convert.converter.Converter;

/**
 * @author intr3p1d
 */
public class ApplicationPairsConverter implements Converter<String, ApplicationPairs> {

    private final static ObjectMapper OBJECT_MAPPER = Jackson.newMapper();

    @Override
    public ApplicationPairs convert(String source) {
        try {
            return OBJECT_MAPPER.readValue(source, ApplicationPairs.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
