package ce316project.entities;

import java.time.Duration;

public class CompilationResult {

    private boolean success;
    private String outputPath;
    private String compilerOutput;
    private Duration compiliationTime;

    public CompilationResult(boolean success, String outputPath, String compilerOutput, Duration compiliationTime) {
        this.success = success;
        this.outputPath = outputPath;
        this.compilerOutput = compilerOutput;
        this.compiliationTime = compiliationTime;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public String getCompilerOutput() {
        return compilerOutput;
    }

    public void setCompilerOutput(String compilerOutput) {
        this.compilerOutput = compilerOutput;
    }

    public Duration getCompiliationTime() {
        return compiliationTime;
    }

    public void setCompiliationTime(Duration compiliationTime) {
        this.compiliationTime = compiliationTime;
    }
    
}
