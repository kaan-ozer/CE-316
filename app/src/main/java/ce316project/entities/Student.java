package ce316project.entities;

import ce316project.utils.Status;

public class Student {
    private String studentId;
    private String directoryPath;
    private ExecutionResult executionResult;
    private CompilationResult compilationResult;
    private Status status;

    public Student(String studentId, String directoryPath)
    {
        this.studentId = studentId;
        this.directoryPath = directoryPath;
        this.status = Status.INITIAL;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getDirectoryPath() {
        return directoryPath;
    }

    public ExecutionResult getExecutionResult() {
        return executionResult;
    }

    public void setExecutionResult(ExecutionResult executionResult) {
        this.executionResult = executionResult;
    }

    public CompilationResult getCompilationResult() {
        return compilationResult;
    }

    public void setCompilationResult(CompilationResult compilationResult) {
        this.compilationResult = compilationResult;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
