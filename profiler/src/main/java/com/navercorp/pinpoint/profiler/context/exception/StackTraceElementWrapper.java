package com.navercorp.pinpoint.profiler.context.exception;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author intr3p1d
 */
public class StackTraceElementWrapper {

    private final String className;
    private final String fileName;
    private final int lineNumber;
    private final String methodName;

    private StackTraceElementWrapper(String className,
                                     String fileName,
                                     int lineNumber,
                                     String methodName) {
        this.className = className;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
        this.methodName = methodName;
    }


    public static List<StackTraceElementWrapper> valueOf(StackTraceElement[] stackTraceElements) {
        return Arrays.stream(stackTraceElements)
                .map(StackTraceElementWrapper::valueOf)
                .collect(Collectors.toList());
    }

    public static StackTraceElementWrapper valueOf(StackTraceElement stackTraceElement) {
        return new StackTraceElementWrapper(
                stackTraceElement.getClassName(),
                stackTraceElement.getFileName(),
                stackTraceElement.getLineNumber(),
                stackTraceElement.getMethodName()
        );
    }

    public String getClassName() {
        return className;
    }

    public String getFileName() {
        return fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getMethodName() {
        return methodName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StackTraceElementWrapper that = (StackTraceElementWrapper) o;
        return lineNumber == that.lineNumber && className.equals(that.className) && fileName.equals(that.fileName) && methodName.equals(that.methodName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, fileName, lineNumber, methodName);
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
