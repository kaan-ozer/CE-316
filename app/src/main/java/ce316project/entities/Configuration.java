package ce316project.entities;

import java.util.List;

public class Configuration {


    private String configName;
    private String language;
    private String compilerPath;
    private String compilerCommand;
    private List<String> compilerParameters;
    private String runCommand;
    private List<String> runParameters;
    private String executableExtension;

    public Configuration(String configName, String executableExtension, String language, String compilerCommand,
                         List<String> compilerParameters, String runCommand, List<String> runParameters, String compilerPath) {
        this.configName = configName;
        this.language = language;
        this.compilerPath = compilerPath;
        this.compilerCommand = compilerCommand;
        this.compilerParameters = compilerParameters;
        this.runCommand = runCommand;
        this.runParameters = runParameters;
        this.executableExtension = executableExtension;
    }

    public Configuration() {
        // Empty constructor for JSON serialization
    }

    public String getConfigName() { return configName; }
    public String getLanguage() { return language; }
    public String getCompilerPath() { return compilerPath; }
    public String getCompilerCommand() { return compilerCommand; }
    public List<String> getCompilerParameters() { return compilerParameters; }
    public String getRunCommand() { return runCommand; }
    public List<String> getRunParameters() { return runParameters; }
    public String getExecutableExtension() { return executableExtension; }

    public void setConfigName(String configId) { this.configName = configId; }
    public void setLanguage(String language) { this.language = language; }
    public void setCompilerPath(String compilerPath) { this.compilerPath = compilerPath; }
    public void setCompilerCommand(String compilerCommand) { this.compilerCommand = compilerCommand; }
    public void setCompilerParameters(List<String> compilerParameters) { this.compilerParameters = compilerParameters; }
    public void setRunCommand(String runCommand) { this.runCommand = runCommand; }
    public void setRunParameters(List<String> runParameters) { this.runParameters = runParameters; }
    public void setExecutableExtension(String executableExtension) { this.executableExtension = executableExtension; }
}
