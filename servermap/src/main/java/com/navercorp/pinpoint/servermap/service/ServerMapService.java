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
package com.navercorp.pinpoint.servermap.service;

import com.navercorp.pinpoint.servermap.dao.InboundDao;
import com.navercorp.pinpoint.servermap.dao.OutboundDao;
import com.navercorp.pinpoint.servermap.dao.SelfDao;
import org.springframework.stereotype.Service;

/**
 * @author intr3p1d
 */
@Service
public class ServerMapService {

    InboundDao hbaseInboundDao;
    OutboundDao hbaseOutboundDao;
    SelfDao hbaseSelfDao;


    public void updateInboundData() {

    }



}
