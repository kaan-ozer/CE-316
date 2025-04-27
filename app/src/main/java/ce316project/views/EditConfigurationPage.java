package ce316project.views;


import ce316project.entities.Configuration;
import com.owlike.genson.Genson;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class EditConfigurationPage extends VBox {
    private Stage stageReference;
    private ComboBox<String> configSelector;
    private TextField configNameField;
    private TextField progLangField;
    private TextField executableExtensionField;
    private TextField compilerCommandField;
    private TextField compilerParametersField;
    private TextField runCommandField;
    private TextField runParametersField;
    private CheckBox compilerInstalledCheckBox;
    private TextField compilerPathField;
    private Button selectCompilerButton;

    public EditConfigurationPage(Stage stageReference) {
        this.stageReference = stageReference;

        this.setPadding(new Insets(20));
        this.setSpacing(15);
        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle("-fx-background-color: #D3D3D3;");

        configSelector = new ComboBox<>();
        configSelector.setPromptText("Select Configuration");
        loadConfigurationFiles();

        configSelector.setOnAction(e -> {
            String selectedConfig = configSelector.getValue();
            if (selectedConfig != null) {
                loadConfiguration(selectedConfig);
            }
        });

        configNameField = new TextField();
        configNameField.setPromptText("Configuration Name");

        progLangField = new TextField();
        progLangField.setPromptText("Programming Language");

        executableExtensionField = new TextField();
        executableExtensionField.setPromptText("Executable Extension");

        compilerCommandField = new TextField();
        compilerCommandField.setPromptText("Compiler Command");

        compilerParametersField = new TextField();
        compilerParametersField.setPromptText("Compiler Parameters");

        runCommandField = new TextField();
        runCommandField.setPromptText("Run Command");

        runParametersField = new TextField();
        runParametersField.setPromptText("Run Parameters");

        compilerInstalledCheckBox = new CheckBox("Compiler is installed (in PATH)");
        compilerInstalledCheckBox.setSelected(true);

        compilerPathField = new TextField();
        compilerPathField.setPromptText("Compiler Path (if not installed)");
        compilerPathField.setEditable(false);
        compilerPathField.setVisible(false);

        selectCompilerButton = new Button("Select Compiler");
        selectCompilerButton.setVisible(false);

        selectCompilerButton.setOnAction(e -> openCompilerChooser());

        HBox compilerPathBox = new HBox(10, compilerPathField, selectCompilerButton);
        compilerPathBox.setAlignment(Pos.CENTER);

        compilerInstalledCheckBox.setOnAction(e -> {
            if (!compilerInstalledCheckBox.isSelected()) {
                compilerPathField.setVisible(true);
                selectCompilerButton.setVisible(true);
            } else {
                compilerPathField.clear();
                compilerPathField.setVisible(false);
                selectCompilerButton.setVisible(false);
            }
        });

        Button saveButton = new Button("Save Changes");
        saveButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-size: 14px;");
        saveButton.setOnAction(e -> saveConfiguration());

        this.getChildren().addAll(
                configSelector,
                new Label("Configuration Name:"), configNameField,
                new Label("Programming Language:"), progLangField,
                new Label("Executable Extension:"), executableExtensionField,
                new Label("Compiler Command:"), compilerCommandField,
                new Label("Compiler Parameters (space separated):"), compilerParametersField,
                new Label("Run Command:"), runCommandField,
                new Label("Run Parameters (space separated):"), runParametersField,
                compilerInstalledCheckBox,
                compilerPathBox,
                saveButton
        );
    }

    private void loadConfigurationFiles() {
        Path outputDir = Paths.get(System.getProperty("user.home"), "Desktop", "CE316", "Project", "CE-316", "app", "src", "main", "java", "ce316project", "output");
        File folder = outputDir.toFile();

        if (!folder.exists()) {
            folder.mkdirs();
        }

        File[] files = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
        if (files != null) {
            for (File file : files) {
                configSelector.getItems().add(file.getName().replace(".json", ""));
            }
        }
    }

    private void loadConfiguration(String configName) {
        Path outputDir = Paths.get(System.getProperty("user.home"), "Desktop", "CE316", "Project", "CE-316", "app", "src", "main", "java", "ce316project", "output");
        File configFile = outputDir.resolve(configName + ".json").toFile();

        try (FileReader reader = new FileReader(configFile)) {
            Genson genson = new Genson();
            Configuration config = genson.deserialize(reader, Configuration.class);

            configNameField.setText(config.getConfigName());
            progLangField.setText(config.getLanguage());
            executableExtensionField.setText(config.getExecutableExtension());
            compilerCommandField.setText(config.getCompilerCommand());
            compilerParametersField.setText(String.join(" ", config.getCompilerParameters()));
            runCommandField.setText(config.getRunCommand());
            runParametersField.setText(String.join(" ", config.getRunParameters()));

            if (config.getCompilerPath() == null || config.getCompilerPath().isEmpty()) {
                compilerInstalledCheckBox.setSelected(true);
                compilerPathField.setVisible(false);
                selectCompilerButton.setVisible(false);
            } else {
                compilerInstalledCheckBox.setSelected(false);
                compilerPathField.setVisible(true);
                selectCompilerButton.setVisible(true);
                compilerPathField.setText(config.getCompilerPath());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openCompilerChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Compiler Executable (e.g., gcc.exe)");

        File selectedFile = fileChooser.showOpenDialog(stageReference);

        if (selectedFile != null) {
            compilerPathField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void saveConfiguration() {
        String configName = configNameField.getText();
        String language = progLangField.getText();
        String compilerCommand = compilerCommandField.getText();
        String compilerParameters = compilerParametersField.getText();
        String runCommand = runCommandField.getText();
        String runParameters = runParametersField.getText();
        String executableExtension = executableExtensionField.getText();

        if (configName.isEmpty() || language.isEmpty() || compilerCommand.isEmpty() || compilerParameters.isEmpty()
                || runCommand.isEmpty() || runParameters.isEmpty() || executableExtension.isEmpty()) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText("Please fill all fields!");
            error.showAndWait();
            return;
        }

        String compilerPath;
        if (compilerInstalledCheckBox.isSelected()) {
            compilerPath = "";
        } else {
            compilerPath = compilerPathField.getText();
            if (compilerPath.isEmpty()) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText("Please select a compiler executable!");
                error.showAndWait();
                return;
            }
        }

        Path outputDir = Paths.get(System.getProperty("user.home"), "Desktop", "CE316", "Project", "CE-316", "app", "src", "main", "java", "ce316project", "output");

        if (!outputDir.toFile().exists()) {
            outputDir.toFile().mkdirs();
        }

        Configuration updatedConfig = new Configuration(
                configName,
                executableExtension,
                language,
                compilerCommand,
                Arrays.asList(compilerParameters.split("\\s+")),
                runCommand,
                Arrays.asList(runParameters.split("\\s+")),
                compilerPath
        );

        Genson genson = new Genson();
        String json = genson.serialize(updatedConfig);

        File outputFile = outputDir.resolve(configName + ".json").toFile();
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setHeaderText("Configuration updated successfully!");
        success.showAndWait();
    }
}
