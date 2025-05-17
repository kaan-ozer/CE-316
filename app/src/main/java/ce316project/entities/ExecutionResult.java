package ce316project.entities;
public class ExecutionResult {
    private int exitCode;
    private String stdOutput;
    private String stdError;
    private String executionDuration;
    
    public ExecutionResult(int exitCode, String stdOutput, String stdError, String executionDuration) {
        this.exitCode = exitCode;
        this.stdOutput = stdOutput;
        this.stdError = stdError;
        this.executionDuration = executionDuration;
    }

    public ExecutionResult(){}

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public String getStdOutput() {
        return stdOutput;
    }

    public void setStdOutput(String stdOutput) {
        this.stdOutput = stdOutput;
    }

    public String getStdError() {
        return stdError;
    }

    public void setStdError(String stdError) {
        this.stdError = stdError;
    }

    public String getExecutionDuration() {
        return executionDuration;
    }

    public void setExecutionDuration(String executionDuration) {
        this.executionDuration = executionDuration;
    }
}
