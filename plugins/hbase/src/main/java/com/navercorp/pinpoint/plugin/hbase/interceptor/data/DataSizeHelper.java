/*
 *  Copyright 2018 NAVER Corp.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.navercorp.pinpoint.plugin.hbase.interceptor.data;

import com.navercorp.pinpoint.common.util.ArrayUtils;
import com.navercorp.pinpoint.plugin.hbase.HbasePluginConstants;

/**
 * @author jimo
 **/
public class DataSizeHelper {

    private static final DataSizeProvider[] MUTATION_SIZE_PROVIDERS = new DataSizeProvider[]{
            new MutationListSizeProvider(),
            new MutationSizeProvider()
    };

    private static final DataSizeProvider[] ROW_MUTATION_SIZE_PROVIDERS = new DataSizeProvider[]{
            new RowMutationSizeProvider(),
    };

    private static final DataSizeProvider[] RESULT_SIZE_PROVIDERS = new DataSizeProvider[]{
            new ResultSizeProvider(),
            new ResultListSizeProvider()
    };

    private DataSizeHelper() {
    }

    public static boolean checkIfMutationOp(String methodName) {
        return HbasePluginConstants.mutationMethodNames.contains(methodName);
    }

    public static boolean checkIfRowMutationOp(String methodName) {
        return HbasePluginConstants.rowMutationMethodNames.contains(methodName);
    }

    public static boolean checkIfGetResultOp(String methodName) {
        return HbasePluginConstants.getResultMethodNames.contains(methodName);
    }

    /**
     * Calculate the last arg data size of write method.
     */
    public static int getMutationSize(Object[] args) {
        if (ArrayUtils.getLength(args) == 0) {
            return 0;
        }
        Object arg = args[args.length - 1];
        return getDataSizeFrom(arg, MUTATION_SIZE_PROVIDERS);
    }

    public static int getRowMutationSize(Object[] args) {
        if (ArrayUtils.getLength(args) == 0) {
            return 0;
        }
        Object arg = args[args.length - 1];
        return getDataSizeFrom(arg, ROW_MUTATION_SIZE_PROVIDERS);
    }

    /**
     * Calculate the result data size of read method
     */
    public static int getResultSize(Object result) {
        if (result == null) {
            return 0;
        }
        return getDataSizeFrom(result, RESULT_SIZE_PROVIDERS);
    }

    private static int getDataSizeFrom(Object o, DataSizeProvider[] dataSizeProviders) {
        for (DataSizeProvider dp : dataSizeProviders) {
            if (dp.isProviderOf(o)) {
                return dp.getDataSize(o);
            }
        }
        return 0;
    }

}