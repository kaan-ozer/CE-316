package ce316project.entities;
public class CompilationResult {

    private boolean success;
    private String outputPath;
    private String compilerOutput;
    private String compilationTime;

    public CompilationResult(boolean success, String outputPath, String compilerOutput, String compilationTime) {
        this.success = success;
        this.outputPath = outputPath;
        this.compilerOutput = compilerOutput;
        this.compilationTime = compilationTime;
    }
    public CompilationResult() {}

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

    public String getCompilationTime() {
        return compilationTime;
    }

    public void setCompilationTime(String compilationTime) {
        this.compilationTime = compilationTime;
    }

}
