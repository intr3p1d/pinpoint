package com.navercorp.pinpoint.plugin.hbase.interceptor.data;

/**
 * @author Woonduk Kang(emeroad)
 */
public class DataOperationType {
    public static final int DISABLE = 0;
    public static final int MUTATION = 1;
    public static final int ROW_MUTATION = 2;
    public static final int RESULT = 3;


    private DataOperationType() {
    }

    public static int resolve(boolean enable, String methodName) {
        if (!enable) {
            return DISABLE;
        }
        if (DataSizeHelper.checkIfMutationOp(methodName)) {
            return MUTATION;
        } else if (DataSizeHelper.checkIfRowMutationOp(methodName)) {
            return ROW_MUTATION;
        } else if (DataSizeHelper.checkIfGetResultOp(methodName)) {
            return RESULT;
        }
        return DISABLE;
    }
}
