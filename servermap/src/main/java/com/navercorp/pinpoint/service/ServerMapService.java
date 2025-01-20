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
package com.navercorp.pinpoint.service;

import org.springframework.stereotype.Service;

import java.util.Timer;

/**
 * @author intr3p1d
 */
@Service
public class ServerMapService {

    // Timer Service to read data from the database
    // and update the server map

    public void updatePeriodically() {
        // Timer Service
        // Update the server map every 5 minutes

        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new ServerMapTask(), 0, 5 * 60 * 1000);
    }



}
