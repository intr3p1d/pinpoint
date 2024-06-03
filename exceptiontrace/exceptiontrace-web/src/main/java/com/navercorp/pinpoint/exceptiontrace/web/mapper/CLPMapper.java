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
package com.navercorp.pinpoint.exceptiontrace.web.mapper;


import java.nio.charset.StandardCharsets;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author intr3p1d
 */
public class CLPMapper {

    // Special placeholder characters for CLP-encoded log type.
    public final static char DICTIONARY_VARIABLE_VALUE = '\u0011';
    public final static char NON_DICTIONARY_VALUE = '\u0012';
    public final static String DICTIONARY_REPLACEMENT = "<ClpPlaceHolder dict %d/>";
    public final static String NON_DICTIONARY_REPLACEMENT = "<ClpPlaceHolder non-dict %d/>";

    static String fixEncodingAndBeautify(String encodedLogType) {
        return beautifyDictPlaceHolder(beautifyNonDictPlaceHolder(
                (makeReadableString(
                        encodedLogType
                ))
        ));
    }

    static String makeReadableString(String isoString) {
        byte[] encodedLogTypeBytes = isoString.getBytes(StandardCharsets.ISO_8859_1);
        return new String(encodedLogTypeBytes, StandardCharsets.UTF_8);
    }

//    static String escapeXml(String rawString) {
//        return StringEscapeUtils.escapeXml11(rawString);
//    }

    static String replacePlaceHolders(String encodedLogType) {
        return encodedLogType
                .replaceAll(String.valueOf(DICTIONARY_VARIABLE_VALUE), DICTIONARY_REPLACEMENT)
                .replaceAll(String.valueOf(NON_DICTIONARY_VALUE), NON_DICTIONARY_REPLACEMENT);
    }


    static String beautifyNonDictPlaceHolder(String encodedLogType) {
        return replaceHolder(
                encodedLogType,
                String.valueOf((NON_DICTIONARY_VALUE)),
                x -> stringFunction(NON_DICTIONARY_REPLACEMENT, x)
        );
    }

    static String beautifyDictPlaceHolder(String encodedLogType) {
        return replaceHolder(
                encodedLogType,
                String.valueOf((DICTIONARY_VARIABLE_VALUE)),
                x -> stringFunction(DICTIONARY_REPLACEMENT, x)
        );
    }

    static String stringFunction(String format, int index) {
        return String.format(format, index);
    }

    static String replaceHolder(String encodedLogType, String replacedCharacter, Function<Integer, String> replacement) {
        Pattern pattern = Pattern.compile(replacedCharacter);
        Matcher matcher = pattern.matcher(encodedLogType);
        StringBuilder result = new StringBuilder();
        int wordCount = 0;

        while (matcher.find()) {
            matcher.appendReplacement(result, replacement.apply(wordCount++));
        }
        matcher.appendTail(result);

        return result.toString();
    }
}
