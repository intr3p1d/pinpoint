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
package com.navercorp.pinpoint.exceptiontrace.web;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.pinot.client.PinotResultSet;
import org.apache.pinot.client.ResultSet;
import org.apache.pinot.client.ResultTableResultSet;
import org.apache.pinot.spi.utils.JsonUtils;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.apache.pinot.client.PinotResultSet.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author intr3p1d
 */
public class PinotResultSetTest {

    public static PinotResultSet fromJson(String jsonText) {
        try {
            JsonNode brokerResponse = JsonUtils.stringToJsonNode(jsonText);
            ResultSet resultSet = new ResultTableResultSet(brokerResponse.get("resultTable"));
            return new PinotResultSet(resultSet);
        } catch (Exception e) {
            return empty();
        }
    }

    static class ErrorMessageEntity {
        private String errorMessage;

        public ErrorMessageEntity() {
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }

    @Test
    void testFromJson() throws SQLException {
        String string = "{\n" +
                "  \"resultTable\": {\n" +
                "    \"dataSchema\": {\n" +
                "      \"columnNames\": [\n" +
                "        \"errorMessage\"\n" +
                "      ],\n" +
                "      \"columnDataTypes\": [\n" +
                "        \"STRING\"\n" +
                "      ]\n" +
                "    },\n" +
                "    \"rows\": [\n" +
                "      [\n" +
                "        \"null\"\n" +
                "      ]\n" +
                "    ]\n" +
                "  },\n" +
                "  \"requestId\": \"294132923012824293\",\n" +
                "  \"brokerId\": \"Broker_dev-pinot-broker-0.dev-pinot-broker-headless.n3r-project-pinot-dev.svc.cluster.local_8099\",\n" +
                "  \"exceptions\": [],\n" +
                "  \"numServersQueried\": 10,\n" +
                "  \"numServersResponded\": 10,\n" +
                "  \"numSegmentsQueried\": 1856,\n" +
                "  \"numSegmentsProcessed\": 1856,\n" +
                "  \"numSegmentsMatched\": 27,\n" +
                "  \"numConsumingSegmentsQueried\": 64,\n" +
                "  \"numConsumingSegmentsProcessed\": 64,\n" +
                "  \"numConsumingSegmentsMatched\": 1,\n" +
                "  \"numDocsScanned\": 27,\n" +
                "  \"numEntriesScannedInFilter\": 5430,\n" +
                "  \"numEntriesScannedPostFilter\": 27,\n" +
                "  \"numGroupsLimitReached\": false,\n" +
                "  \"totalDocs\": 365915,\n" +
                "  \"timeUsedMs\": 12,\n" +
                "  \"offlineThreadCpuTimeNs\": 0,\n" +
                "  \"realtimeThreadCpuTimeNs\": 0,\n" +
                "  \"offlineSystemActivitiesCpuTimeNs\": 0,\n" +
                "  \"realtimeSystemActivitiesCpuTimeNs\": 0,\n" +
                "  \"offlineResponseSerializationCpuTimeNs\": 0,\n" +
                "  \"realtimeResponseSerializationCpuTimeNs\": 0,\n" +
                "  \"offlineTotalCpuTimeNs\": 0,\n" +
                "  \"realtimeTotalCpuTimeNs\": 0,\n" +
                "  \"brokerReduceTimeMs\": 0,\n" +
                "  \"segmentStatistics\": [],\n" +
                "  \"traceInfo\": {},\n" +
                "  \"minConsumingFreshnessTimeMs\": 1711960858603,\n" +
                "  \"numSegmentsPrunedByBroker\": 0,\n" +
                "  \"numSegmentsPrunedByServer\": 0,\n" +
                "  \"numSegmentsPrunedInvalid\": 0,\n" +
                "  \"numSegmentsPrunedByLimit\": 0,\n" +
                "  \"numSegmentsPrunedByValue\": 0,\n" +
                "  \"explainPlanNumEmptyFilterSegments\": 0,\n" +
                "  \"explainPlanNumMatchAllFilterSegments\": 0,\n" +
                "  \"numRowsResultSet\": 1\n" +
                "}";
        PinotResultSet resultSet = fromJson(string);

        // assertEquals("null", resultSet.getString(1));

    }

}
