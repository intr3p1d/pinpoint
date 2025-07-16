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
package com.navercorp.pinpoint.web.applicationmap.controller.form;

import com.navercorp.pinpoint.common.trace.ServiceType;
import jakarta.validation.constraints.NotBlank;

/**
 * @author intr3p1d
 */
public class NodeForm {

    public static final int UNDEFINED = ServiceType.UNDEFINED.getCode();

    @NotBlank
    private String nodeName;
    private int nodeServiceTypeCode = UNDEFINED;
    private String nodeServiceTypeName;

    public NodeForm() {
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public int getNodeServiceTypeCode() {
        return nodeServiceTypeCode;
    }

    public void setNodeServiceTypeCode(int nodeServiceTypeCode) {
        this.nodeServiceTypeCode = nodeServiceTypeCode;
    }

    public String getNodeServiceTypeName() {
        return nodeServiceTypeName;
    }

    public void setNodeServiceTypeName(String nodeServiceTypeName) {
        this.nodeServiceTypeName = nodeServiceTypeName;
    }

    @Override
    public String toString() {
        return "NodeForm{" +
                "nodeName='" + nodeName + '\'' +
                ", nodeServiceTypeCode=" + nodeServiceTypeCode +
                ", nodeServiceTypeName='" + nodeServiceTypeName + '\'' +
                '}';
    }
}
