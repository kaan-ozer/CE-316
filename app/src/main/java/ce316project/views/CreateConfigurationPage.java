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

        Label compilerParamsLabel = new Label("Compiler Parameters (separate by space):");
        TextField compilerParamsField = new TextField();
        compilerParamsField.setPromptText("e.g., -o outputFile");

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
                saveConfiguration(configNameField, progLangField, compilerParamsField, runParamsField, executableExtensionField, sourceExtensionField);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        this.getChildren().addAll(
                configNameLabel, configNameField,
                progLangLabel, progLangField,
                executableExtensionLabel, executableExtensionField,
                sourceExtensionLabel, sourceExtensionField,
                compilerParamsLabel, compilerParamsField,
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

    private void saveConfiguration(TextField configNameField, TextField progLangField,
                                   TextField compilerParamsField, TextField runParamsField,
                                   TextField executableExtensionField, TextField sourceExtensionField) throws IOException {

        String configName = configNameField.getText().trim();
        String language = progLangField.getText().trim().toLowerCase();
        String compilerParameters = compilerParamsField.getText().trim();
        String runParameters = runParamsField.getText().trim();
        String executableExtension = executableExtensionField.getText().trim();
        String sourceExtension = sourceExtensionField.getText().trim();
        String compilerPath = compilerInstalledCheckBox.isSelected() ? "" : compilerPathField.getText().trim();

        StringBuilder missingFields = new StringBuilder();

        if (configName.isEmpty()) missingFields.append("- Configuration Name\n");
        if (language.isEmpty()) missingFields.append("- Programming Language\n");
        if (executableExtension.isEmpty()) missingFields.append("- Executable File Extension\n");
        if (sourceExtension.isEmpty()) missingFields.append("- Source File Extension\n");

        boolean needsCompiler = language.equals("c") || language.equals("c++") || language.equals("cpp") || language.equals("java");

        if (needsCompiler) {
            if (!compilerInstalledCheckBox.isSelected() && compilerPath.isEmpty()) {
                missingFields.append("- Compiler Path (since compiler is not installed)\n");
            }
        }

        if (missingFields.length() > 0) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText("Please fill the following required field(s):");
            error.setContentText(missingFields.toString());
            error.showAndWait();
            return;
        }

        Path configsDir = Paths.get(System.getProperty("user.dir"), "configs");

        if (!configsDir.toFile().exists()) {
            configsDir.toFile().mkdirs();
        }

        File outputFile = configsDir.resolve(configName + ".json").toFile();
        if (outputFile.exists()) {
            Alert duplicateAlert = new Alert(Alert.AlertType.ERROR);
            duplicateAlert.setTitle("Duplicate Configuration Name");
            duplicateAlert.setHeaderText("A configuration with the same name already exists.");
            duplicateAlert.setContentText("Please choose a different configuration name.");
            duplicateAlert.showAndWait();
            return;
        }

        Configuration config = new Configuration(
                configName,
                executableExtension,
                language,
                compilerParameters.isEmpty() ? Arrays.asList() : Arrays.asList(compilerParameters.split("\\s+")),
                runParameters.isEmpty() ? Arrays.asList() : Arrays.asList(runParameters.split("\\s+")),
                compilerPath,
                sourceExtension
        );

        Genson genson = new Genson();
        String json = genson.serialize(config);

        try (FileWriter writer = new FileWriter(outputFile)) {
            writer.write(json);
            writer.flush();
        }

        if (onSaveCallback != null) {
            onSaveCallback.run();
        }

        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setHeaderText("Configuration saved successfully!");
        success.setContentText("Saved at: " + outputFile.getAbsolutePath());
        success.showAndWait();
    }
}
