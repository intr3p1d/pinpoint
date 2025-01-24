/*
 * Copyright 2025 NAVER Corp.
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
package com.navercorp.pinpoint.servermap.bo;

import java.util.List;
import java.util.Objects;

/**
 * @author intr3p1d
 */
public class DirectionalBo {
//    ts:tableKind:tenantId:mainServiceId:mainApplicationName:
//    subServiceId:subApplicationName:subServiceTypeSlot

    private final TableName tableName;
    private final String tenantId;
    private final String mainServiceId;
    private final String mainApplicationName;
    private final String subServiceId;
    private final String subApplicationName;
    private final int subServiceTypeSlot;

    private List<CallCount> callCountList;


    public DirectionalBo(
            String tableName,
            String tenantId,
            String mainServiceId, String mainApplicationName,
            String subServiceId, String subApplicationName,
            int subServiceTypeSlot
    ) {
        Objects.requireNonNull(tableName, "tableName");
        this.tableName = TableName.of(tableName);
        this.tenantId = Objects.requireNonNull(tenantId, "tenantId");
        this.mainServiceId = Objects.requireNonNull(mainServiceId, "mainServiceId");
        this.mainApplicationName = Objects.requireNonNull(mainApplicationName, "mainApplicationName");
        this.subServiceId = Objects.requireNonNull(subServiceId, "subServiceId");
        this.subApplicationName = Objects.requireNonNull(subApplicationName, "subApplicationName");
        this.subServiceTypeSlot = subServiceTypeSlot;
    }

    public static DirectionalBo fromKey(String key) {
        String[] parts = key.split(":");
        if (!Objects.equals(parts[0], "ts")) {
            throw new IllegalArgumentException("Invalid key format: " + key);
        }
        if (parts.length != 8) {
            throw new IllegalArgumentException("Invalid key format: " + key);
        }

        return new DirectionalBo(parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], Integer.parseInt(parts[7]));
    }

    public TableName getTableName() {
        return tableName;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getMainServiceId() {
        return mainServiceId;
    }

    public String getMainApplicationName() {
        return mainApplicationName;
    }

    public String getSubServiceId() {
        return subServiceId;
    }

    public String getSubApplicationName() {
        return subApplicationName;
    }

    public int getSubServiceTypeSlot() {
        return subServiceTypeSlot;
    }

    public List<CallCount> getCallCountList() {
        return callCountList;
    }

    public void setCallCountList(List<CallCount> callCountList) {
        this.callCountList = callCountList;
    }

    @Override
    public String toString() {
        return "DirectionalBo{" +
                "tableName='" + tableName + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", mainServiceId='" + mainServiceId + '\'' +
                ", mainApplicationName='" + mainApplicationName + '\'' +
                ", subServiceId='" + subServiceId + '\'' +
                ", subApplicationName='" + subApplicationName + '\'' +
                ", subServiceTypeSlot=" + subServiceTypeSlot +
                ", callCountList=" + callCountList +
                '}';
    }
}
