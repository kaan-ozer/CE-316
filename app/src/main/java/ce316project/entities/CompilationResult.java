package ce316project.entities;
public class CompilationResult {

    private boolean success;
    private String outputPath;
    private String compilerOutput;
    private String compiliationTime;

    public CompilationResult(boolean success, String outputPath, String compilerOutput, String compiliationTime) {
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

    public String getCompiliationTime() {
        return compiliationTime;
    }

    public void setCompiliationTime(String compiliationTime) {
        this.compiliationTime = compiliationTime;
    }
    
}
