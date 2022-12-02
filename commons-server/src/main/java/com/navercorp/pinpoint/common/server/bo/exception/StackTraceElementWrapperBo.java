package com.navercorp.pinpoint.common.server.bo.exception;

/**
 * @author intr3p1d
 */
public class StackTraceElementWrapperBo {
    private String className;
    private String fileName;
    private int lineNumber;
    private String methodName;

    public StackTraceElementWrapperBo() {
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
