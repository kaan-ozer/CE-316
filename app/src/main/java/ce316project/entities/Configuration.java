package ce316project.entities;

import com.owlike.genson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Configuration {


    private String configName;
    private String language;
    private String compilerPath;
    private List<String> compilerParameters;
    private List<String> runParameters;
    private String executableExtension;
    private String sourceExtension;

    public Configuration(String configName, String executableExtension, String language,
                         List<String> compilerParameters, List<String> runParameters, String compilerPath, String sourceExtension) {
        this.configName = configName;
        this.language = language;
        this.compilerPath = compilerPath;
        this.compilerParameters = compilerParameters;
        this.runParameters = runParameters;
        this.executableExtension = executableExtension;
        this.sourceExtension = sourceExtension;
    }

    public Configuration() {
        // Empty constructor for JSON serialization
    }

    public String getConfigName() {
        return configName;
    }

    public String getLanguage() {
        return language;
    }

    public String getCompilerPath() {
        return compilerPath;
    }

    public String getExecutableExtension() {
        return executableExtension;
    }

    public String getSourceExtension() {
        return sourceExtension;
    }

    public List<String> getCompilerParameters() {
        return compilerParameters;
    }

    @JsonIgnore
    public String getCompilerCommand() {
        if (compilerParameters != null && !compilerParameters.isEmpty() && compilerParameters.get(0) != null) {
            return compilerParameters.get(0);
        }
        return "";
    }

    @JsonIgnore
    public List<String> getRemainingCompilerParameters() {
        if (compilerParameters == null || compilerParameters.size() <= 1)
            return Collections.emptyList();
        return new ArrayList<>(compilerParameters.subList(1, compilerParameters.size()));
    }


    public List<String> getRunParameters() {
        return runParameters;
    }

    @JsonIgnore
    public String getRunCommand() {
        if (runParameters != null && !runParameters.isEmpty() && runParameters.get(0) != null) {
            return runParameters.get(0);
        }
        return "";
    }

    @JsonIgnore
    public List<String> getRemainingRunParameters() {
        if (runParameters == null || runParameters.size() <= 1)
            return Collections.emptyList();
        return new ArrayList<>(runParameters.subList(1, runParameters.size()));
    }

    public void setConfigName(String configId) {
        this.configName = configId;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setCompilerPath(String compilerPath) {
        this.compilerPath = compilerPath;
    }

    public void setCompilerParameters(List<String> compilerParameters) {
        this.compilerParameters = compilerParameters;
    }

    public void setRunParameters(List<String> runParameters) {
        this.runParameters = runParameters;
    }

    public void setExecutableExtension(String executableExtension) {
        this.executableExtension = executableExtension;
    }

    public void setSourceExtension(String sourceExtension) {
        this.sourceExtension = sourceExtension;
    }


}
