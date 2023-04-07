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
package com.navercorp.pinpoint.exceptiontrace.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hadoop.hbase.shaded.org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @author intr3p1d
 */
public class HashUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static <T> String wrappersToHashString(List<T> objects) {
        String s = objectsToJsonString(objects);
        return toHexString(
                toHashBytes(s)
        );
    }

    private static <T> String objectsToJsonString(List<T> objects) {
        try {
            return OBJECT_MAPPER.writeValueAsString(objects);
        } catch (JsonProcessingException ignored) {
            // do nothing
        }
        return "";
    }

    private static byte[] toHashBytes(String string) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(
                    string.getBytes(StandardCharsets.UTF_8)
            );
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    private static String toHexString(byte[] bytes) {
        return Hex.encodeHexString(bytes);
    }
}
