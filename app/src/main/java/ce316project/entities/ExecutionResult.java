package ce316project.entities;

import java.time.Duration;

public class ExecutionResult {
    private int exitCode;
    private String stdOutput;
    private String stdError;
    private Duration executionDuration;
    
    public ExecutionResult(int exitCode, String stdOutput, String stdError, Duration executionDuration) {
        this.exitCode = exitCode;
        this.stdOutput = stdOutput;
        this.stdError = stdError;
        this.executionDuration = executionDuration;
    }

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

    public Duration getExecutionDuration() {
        return executionDuration;
    }

    public String getMillis() {
        return Integer.toString((int) executionDuration.toMillis());
    }

    public void setExecutionDuration(Duration executionDuration) {
        this.executionDuration = executionDuration;
    }
}
