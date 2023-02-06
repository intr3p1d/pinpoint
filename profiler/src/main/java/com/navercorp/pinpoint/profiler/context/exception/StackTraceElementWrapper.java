package com.navercorp.pinpoint.profiler.context.exception;

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

    protected static StackTraceElementWrapper[] valueOf(StackTraceElement[] stackTraceElements) {
        final int size = stackTraceElements.length;
        StackTraceElementWrapper[] stackTraceElementWrappers = new StackTraceElementWrapper[size];
        for (int i = 0; i < size; i++) {
            stackTraceElementWrappers[i] = valueOf(stackTraceElements[i]);
        }
        return stackTraceElementWrappers;
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

        if (lineNumber != that.lineNumber) return false;
        if (!className.equals(that.className)) return false;
        if (!fileName.equals(that.fileName)) return false;
        return methodName.equals(that.methodName);
    }

    @Override
    public int hashCode() {
        int result = className.hashCode();
        result = 31 * result + fileName.hashCode();
        result = 31 * result + lineNumber;
        result = 31 * result + methodName.hashCode();
        return result;
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
