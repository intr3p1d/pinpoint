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
package com.navercorp.pinpoint.web.applicationmap.service;

import com.navercorp.pinpoint.common.timeseries.window.TimeWindow;
import com.navercorp.pinpoint.web.applicationmap.ApplicationMap;
import com.navercorp.pinpoint.web.applicationmap.map.LinkSelector;
import com.navercorp.pinpoint.web.applicationmap.map.LinkSelectorFactory;
import com.navercorp.pinpoint.web.applicationmap.map.LinkSelectorType;
import com.navercorp.pinpoint.web.applicationmap.map.processor.LinkDataMapProcessor;
import com.navercorp.pinpoint.web.applicationmap.map.processor.WasOnlyProcessor;
import com.navercorp.pinpoint.web.applicationmap.rawdata.LinkDataDuplexMap;
import com.navercorp.pinpoint.web.vo.SearchOption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StopWatch;

import java.util.Collections;
import java.util.Objects;

/**
 * @author intr3p1d
 */
public class HistogramServiceImpl implements HistogramService {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final LinkSelectorFactory linkSelectorFactory;

    public HistogramServiceImpl(
            LinkSelectorFactory linkSelectorFactory
    ) {
        this.linkSelectorFactory = Objects.requireNonNull(linkSelectorFactory, "linkSelectorFactory");
    }

    @Override
    public ApplicationMap selectApplicationMap(MapServiceOption option) {

        logger.debug("SelectApplicationMap");

        StopWatch watch = new StopWatch("ApplicationMap");
        watch.start("ApplicationMap Link Fetch(Out,In) Time");

        final SearchOption searchOption = option.getSearchOption();
        LinkSelectorType linkSelectorType = searchOption.getLinkSelectorType();
        int outSearchDepth = searchOption.getOutSearchDepth();
        int inSearchDepth = searchOption.getInSearchDepth();
        boolean timeAggregate = option.isSimpleResponseHistogram();

        LinkDataMapProcessor outLinkProcessor = LinkDataMapProcessor.NO_OP;
        if (searchOption.isWasOnly()) {
            outLinkProcessor = new WasOnlyProcessor();
        }
        LinkDataMapProcessor inLinkProcessor = LinkDataMapProcessor.NO_OP;
        LinkSelector linkSelector = linkSelectorFactory.createLinkSelector(linkSelectorType, outLinkProcessor, inLinkProcessor);

        TimeWindow timeWindow = option.getTimeWindow();
        LinkDataDuplexMap linkDataDuplexMap = linkSelector.select(Collections.singletonList(option.getSourceApplication()), timeWindow, outSearchDepth, inSearchDepth, timeAggregate);
        watch.stop();


        return null;
    }
}
