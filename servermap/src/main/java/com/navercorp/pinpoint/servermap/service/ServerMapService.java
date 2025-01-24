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

import com.navercorp.pinpoint.servermap.bo.DirectionalBo;
import com.navercorp.pinpoint.servermap.dao.InboundDao;
import com.navercorp.pinpoint.servermap.dao.OutboundDao;
import com.navercorp.pinpoint.servermap.dao.SelfDao;
import com.navercorp.pinpoint.servermap.dao.hbase.HbaseApplicationMapDao;
import com.navercorp.pinpoint.servermap.dao.redis.RedisDao;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author intr3p1d
 */
@Service
public class ServerMapService {
    RedisDao redisDao;
    HbaseApplicationMapDao hbaseApplicationMapDao;

    public ServerMapService(
            RedisDao redisDao,
            HbaseApplicationMapDao hbaseApplicationMapDao
    ) {
        this.redisDao = Objects.requireNonNull(redisDao, "redisDao");
        this.hbaseApplicationMapDao = Objects.requireNonNull(hbaseApplicationMapDao, "hbaseApplicationMapDao");
    }

    public void updateData() {
        List<DirectionalBo> directionalBoList = redisDao.readData();
        for (DirectionalBo directionalBo : directionalBoList) {
            hbaseApplicationMapDao.insert(directionalBo);
        }
    }
}
