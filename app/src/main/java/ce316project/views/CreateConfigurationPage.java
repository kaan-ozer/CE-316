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
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class CreateConfigurationPage extends VBox {
    private Stage stageReference;
    private CheckBox compilerInstalledCheckBox;
    private TextField compilerPathField;
    private Button selectCompilerButton;
    private Runnable onSaveCallback;

    //TODO: validations must be reviewed
    public CreateConfigurationPage(Stage stageReference, Runnable onSaveCallback) {
        this.stageReference = stageReference;
        this.onSaveCallback = onSaveCallback;

        this.setPadding(new Insets(20));
        this.setSpacing(15);
        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle("-fx-background-color: #D3D3D3;");

        Label configNameLabel = new Label("Configuration Name:");
        TextField configNameField = new TextField();
        configNameField.setPromptText("Configuration Name");

        Label progLangLabel = new Label("Programming Language:");
        TextField progLangField = new TextField();
        progLangField.setPromptText("Programming Language");

        Label executableExtensionLabel = new Label("Executable File Extension:");
        TextField executableExtensionField = new TextField();
        executableExtensionField.setPromptText("e.g., .exe or .out");

        Label sourceExtensionLabel = new Label("Source File Extension:");
        TextField sourceExtensionField = new TextField();
        sourceExtensionField.setPromptText("e.g., .c or .java");

        Label compilerCommandLabel = new Label("Compiler Command:");
        TextField compilerCommandField = new TextField();
        compilerCommandField.setPromptText("e.g., gcc");

        Label compilerParamsLabel = new Label("Compiler Parameters (separate by space):");
        TextField compilerParamsField = new TextField();
        compilerParamsField.setPromptText("e.g., -o outputFile");

        Label runCommandLabel = new Label("Run Command:");
        TextField runCommandField = new TextField();
        runCommandField.setPromptText("e.g., ./outputFile");

        Label runParamsLabel = new Label("Run Parameters (separate by space):");
        TextField runParamsField = new TextField();
        runParamsField.setPromptText("e.g., arg1 arg2");

        compilerInstalledCheckBox = new CheckBox("Compiler is already installed and available in system PATH");
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

        Button generateButton = new Button("Generate Configuration");
        generateButton.setStyle("-fx-background-color: #0078D7; -fx-text-fill: white; -fx-font-size: 14px;");

        generateButton.setOnAction(e -> {
            try {
                saveConfiguration(configNameField, progLangField, compilerCommandField, compilerParamsField, runCommandField, runParamsField, executableExtensionField, sourceExtensionField);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        this.getChildren().addAll(
                configNameLabel, configNameField,
                progLangLabel, progLangField,
                executableExtensionLabel, executableExtensionField,
                sourceExtensionLabel, sourceExtensionField,
                compilerCommandLabel, compilerCommandField,
                compilerParamsLabel, compilerParamsField,
                runCommandLabel, runCommandField,
                runParamsLabel, runParamsField,
                compilerInstalledCheckBox,
                compilerPathBox,
                generateButton
        );
    }

    private void openCompilerChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Compiler Executable (e.g., gcc.exe)");

        File selectedFile = fileChooser.showOpenDialog(stageReference);

        if (selectedFile != null) {
            compilerPathField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void saveConfiguration(TextField configNameField, TextField progLangField, TextField compilerCommandField,
                                   TextField compilerParamsField, TextField runCommandField, TextField runParamsField,
                                   TextField executableExtensionField, TextField sourceExtensionField) throws IOException {

        String configName = configNameField.getText();
        String language = progLangField.getText();
        String compilerCommand = compilerCommandField.getText();
        String compilerParameters = compilerParamsField.getText();
        String runCommand = runCommandField.getText();
        String runParameters = runParamsField.getText();
        String executableExtension = executableExtensionField.getText();
        String sourceExtension = sourceExtensionField.getText();

        if (configName.isEmpty() || language.isEmpty() || compilerCommand.isEmpty() || compilerParameters.isEmpty()
                || runCommand.isEmpty() || runParameters.isEmpty() || executableExtension.isEmpty() || sourceExtension.isEmpty()) {
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

        Path configsDir = Paths.get(System.getProperty("user.dir"), "src", "main", "java", "ce316project", "configs");

        if (!configsDir.toFile().exists()) {
            configsDir.toFile().mkdirs();
        }

        Configuration config = new Configuration(
                configName,
                executableExtension,
                language,
                compilerCommand,
                Arrays.asList(compilerParameters.split("\\s+")),
                runCommand,
                Arrays.asList(runParameters.split("\\s+")),
                compilerPath,
                sourceExtension
        );

        Genson genson = new Genson();
        String json = genson.serialize(config);

        File outputFile = configsDir.resolve(configName + ".json").toFile();
        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(json);
        }

        if (onSaveCallback != null) {
            onSaveCallback.run();
        }

        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setHeaderText("Configuration saved successfully to:\n" + outputFile.getAbsolutePath());
        success.showAndWait();
    }

}
