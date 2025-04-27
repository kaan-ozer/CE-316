package ce316project.entities;

import java.nio.file.Path;
import java.util.List;

public class Configuration {

    private String configId;
    private String language;
    private Path compilerPath;
    private String compilerCommand;
    private List<String> compilerParameters;
    private String runCommand;
    private List<String> runParameters;

    public Configuration(String configId, String language, Path compilerPath, String compilerCommand,
            List<String> compilerParameters, String runCommand, List<String> runParameters) {
        this.configId = configId;
        this.language = language;
        this.compilerPath = compilerPath;
        this.compilerCommand = compilerCommand;
        this.compilerParameters = compilerParameters;
        this.runCommand = runCommand;
        this.runParameters = runParameters;
    }

}
