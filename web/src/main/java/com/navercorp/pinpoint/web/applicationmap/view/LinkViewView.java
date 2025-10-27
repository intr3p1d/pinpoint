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
package com.navercorp.pinpoint.web.applicationmap.view;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.navercorp.pinpoint.web.applicationmap.histogram.TimeHistogramFormat;
import com.navercorp.pinpoint.web.applicationmap.link.Link;
import com.navercorp.pinpoint.web.applicationmap.link.LinkName;
import com.navercorp.pinpoint.web.applicationmap.map.MapViews;
import com.navercorp.pinpoint.web.applicationmap.nodes.NodeName;

import java.util.Objects;

/**
 * @author intr3p1d
 */
public class LinkViewView {

    @JsonIgnore
    private final Link link;

    @JsonIgnore
    private final MapViews activeView;

    @JsonIgnore
    private final TimeHistogramFormat format;

    public LinkViewView(Link link, MapViews activeView, TimeHistogramFormat format) {
        this.link = Objects.requireNonNull(link, "link");
        this.activeView = Objects.requireNonNull(activeView, "activeView");
        this.format = Objects.requireNonNull(format, "format");
    }

    public LinkName getKey() {
        return link.getLinkName();
    }

    public String getLinkKey() {
        return link.getLinkNameKey();
    }

    public NodeName getFrom() {
        return link.getFrom().getNodeName();
    }

    public NodeName getTo() {
        return link.getTo().getNodeName();
    }

    public


}
