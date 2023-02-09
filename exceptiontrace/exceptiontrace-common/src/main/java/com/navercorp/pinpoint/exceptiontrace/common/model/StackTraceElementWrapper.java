package com.navercorp.pinpoint.exceptiontrace.common.model;

import java.util.Objects;

/**
 * @author intr3p1d
 */
public class StackTraceElementWrapper {
    private String className;
    private String fileName;
    private int lineNumber;
    private String methodName;

    public StackTraceElementWrapper() {
    }

    public StackTraceElementWrapper(String className,
                                    String fileName,
                                    int lineNumber,
                                    String methodName) {
        this.className = Objects.requireNonNull(className, "className");
        this.fileName = Objects.requireNonNull(fileName, "fileName");
        this.lineNumber = lineNumber;
        this.methodName = Objects.requireNonNull(methodName, "methodName");
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

    @Override
    public String toString() {
        return "StackTraceElementWrapper{" +
                "className='" + className + '\'' +
                ", fileName='" + fileName + '\'' +
                ", lineNumber=" + lineNumber +
                ", methodName='" + methodName + '\'' +
                '}';
    }
}
