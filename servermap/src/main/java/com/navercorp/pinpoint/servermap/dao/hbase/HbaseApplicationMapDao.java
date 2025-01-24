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
package com.navercorp.pinpoint.servermap.dao.hbase;

import com.navercorp.pinpoint.common.hbase.HbaseOperations;
import com.navercorp.pinpoint.common.hbase.TableNameProvider;
import com.navercorp.pinpoint.servermap.bo.DirectionalBo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.Objects;

/**
 * @author intr3p1d
 */
@Repository
public class HbaseApplicationMapDao {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final HbaseOperations hbaseTemplate;
    private final TableNameProvider tableNameProvider;




    public HbaseApplicationMapDao(
            HbaseOperations hbaseTemplate,
            TableNameProvider tableNameProvider
    ) {
        this.hbaseTemplate = Objects.requireNonNull(hbaseTemplate, "hbaseTemplate");
        this.tableNameProvider = Objects.requireNonNull(tableNameProvider, "tableNameProvider");
    }


    public void insert(DirectionalBo directionalBo) {
        Objects.requireNonNull(directionalBo, "directionalBo");
        if (logger.isDebugEnabled()) {
            logger.debug("insert application map data: {}", directionalBo);
        }

        ApplicationMapRowKey rowKey = new ApplicationMapRowKey(
                directionalBo.getMainServiceId(),
                directionalBo.getMainApplicationName(), directionalBo.
        );


    }



}
