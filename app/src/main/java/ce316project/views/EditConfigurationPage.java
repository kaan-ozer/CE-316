package ce316project.views;


import ce316project.entities.Configuration;
import ce316project.entities.Project;
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
    private TextField sourceExtensionField;
    private TextField compilerParametersField;
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

        sourceExtensionField = new TextField();
        sourceExtensionField.setPromptText("Source File Extension");

        compilerParametersField = new TextField();
        compilerParametersField.setPromptText("Compiler Parameters");

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

        Button deleteButton = new Button("Delete Selected Configuration");
        deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 14px;");
        deleteButton.setOnAction(e -> deleteSelectedConfiguration());

        Button importButton = new Button("Import Configuration");
        importButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 14px;");
        importButton.setOnAction(e -> importConfiguration());

        Button exportButton = new Button("Export Configuration");
        exportButton.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-font-size: 14px;");
        exportButton.setOnAction(e -> exportConfiguration());

        HBox buttonsBox = new HBox(20, saveButton, deleteButton, importButton, exportButton);
        buttonsBox.setAlignment(Pos.CENTER);


        this.getChildren().addAll(
                configSelector,
                new Label("Configuration Name:"), configNameField,
                new Label("Programming Language:"), progLangField,
                new Label("Executable Extension:"), executableExtensionField,
                new Label("Source File Extension"), sourceExtensionField,
                new Label("Compiler Parameters (space separated):"), compilerParametersField,
                new Label("Run Parameters (space separated):"), runParametersField,
                compilerInstalledCheckBox,
                compilerPathBox,
                buttonsBox
        );
    }

    private void loadConfigurationFiles() {
        Path configsDir = Paths.get(System.getProperty("user.dir"), "configs");
        File folder = configsDir.toFile();

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
        Path configsDir = Paths.get(System.getProperty("user.dir"), "configs");
        File configFile = configsDir.resolve(configName + ".json").toFile();

        try (FileReader reader = new FileReader(configFile)) {
            Genson genson = new Genson();
            Configuration config = genson.deserialize(reader, Configuration.class);

            configNameField.setText(config.getConfigName());
            progLangField.setText(config.getLanguage());
            executableExtensionField.setText(config.getExecutableExtension());
            sourceExtensionField.setText(config.getSourceExtension());
            compilerParametersField.setText(String.join(" ", config.getCompilerParameters()));
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
        String selectedConfig = configSelector.getValue();
        if (selectedConfig == null || selectedConfig.isEmpty()) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText("No configuration selected!");
            error.setContentText("Please select a configuration to edit.");
            error.showAndWait();
            return;
        }

        String configName = configNameField.getText().trim();
        String language = progLangField.getText().trim();
        String compilerParameters = compilerParametersField.getText().trim();
        String runParameters = runParametersField.getText().trim();
        String executableExtension = executableExtensionField.getText().trim();
        String sourceFileExtension = sourceExtensionField.getText().trim();

        if (configName.isEmpty() || language.isEmpty()) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText("Please fill required fields (Configuration Name, Programming Language)!");
            error.showAndWait();
            return;
        }

        String compilerPath;
        if (compilerInstalledCheckBox.isSelected()) {
            compilerPath = "";
        } else {
            compilerPath = compilerPathField.getText().trim();
            if (compilerPath.isEmpty()) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText("Please select a compiler executable!");
                error.showAndWait();
                return;
            }
        }

        Path configsDir = Paths.get(System.getProperty("user.dir"), "configs");

        if (!configsDir.toFile().exists()) {
            configsDir.toFile().mkdirs();
        }

        File oldFile = configsDir.resolve(selectedConfig + ".json").toFile();
        File newFile = configsDir.resolve(configName + ".json").toFile();

        boolean isRenamed = !selectedConfig.equals(configName);

        if (isRenamed) {
            if (oldFile.exists()) {
                oldFile.delete();
            }
            configSelector.getItems().remove(selectedConfig);
            if (!configSelector.getItems().contains(configName)) {
                configSelector.getItems().add(configName);
            }
        }

        Configuration updatedConfig = new Configuration(
                configName,
                executableExtension,
                language,
                compilerParameters.isEmpty() ? Arrays.asList() : Arrays.asList(compilerParameters.split("\\s+")),
                runParameters.isEmpty() ? Arrays.asList() : Arrays.asList(runParameters.split("\\s+")),
                compilerPath,
                sourceFileExtension
        );

        Genson genson = new Genson();
        String json = genson.serialize(updatedConfig);

        try (FileWriter writer = new FileWriter(newFile)) {
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        updateProjectsUsingThisConfiguration(updatedConfig, selectedConfig);

        configSelector.setValue(configName);
        loadConfiguration(configName);

        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setHeaderText("Configuration updated successfully!");
        success.showAndWait();
    }

    private void updateProjectsUsingThisConfiguration(Configuration updatedConfig, String oldConfigName) {
        Path projectsDir = Paths.get(System.getProperty("user.dir"), "src", "main", "java", "ce316project", "projects");
        File[] projectFiles = projectsDir.toFile().listFiles((dir, name) -> name.endsWith(".json"));

        if (projectFiles == null) return;

        Genson genson = new Genson();

        for (File projectFile : projectFiles) {
            try (FileReader reader = new FileReader(projectFile)) {
                Project project = genson.deserialize(reader, Project.class);

                // Burada hem eski ismi hem de yeni ismi kontrol ediyoruz
                String currentProjectConfigName = project.getConfig().getConfigName();

                if (currentProjectConfigName.equals(oldConfigName) || currentProjectConfigName.equals(updatedConfig.getConfigName())) {
                    project.setConfig(updatedConfig); // yeni config ile değiştir

                    try (FileWriter writer = new FileWriter(projectFile)) {
                        genson.serialize(project, writer); // güncellenmiş projeyi yaz
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void deleteSelectedConfiguration() {
        String selectedConfig = configSelector.getValue();

        if (selectedConfig == null || selectedConfig.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("No configuration selected!");
            alert.setContentText("Please select a configuration to delete.");
            alert.showAndWait();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Confirmation");
        confirmation.setHeaderText("Are you sure you want to delete '" + selectedConfig + "'?");
        confirmation.setContentText("This action cannot be undone.");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Path configsDir = Paths.get(System.getProperty("user.dir"), "configs");
                File configFile = configsDir.resolve(selectedConfig + ".json").toFile();

                if (configFile.exists()) {
                    if (configFile.delete()) {
                        configSelector.getItems().remove(selectedConfig);
                        clearFields();

                        Alert success = new Alert(Alert.AlertType.INFORMATION);
                        success.setHeaderText("Configuration deleted successfully!");
                        success.showAndWait();
                    } else {
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setHeaderText("Failed to delete the configuration file.");
                        error.showAndWait();
                    }
                }
            }
        });
    }

    private void importConfiguration() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Configuration File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        File selectedFile = fileChooser.showOpenDialog(stageReference);

        if (selectedFile == null) {
            return;
        }

        Path configsDir = Paths.get(System.getProperty("user.dir"), "configs");

        if (!configsDir.toFile().exists()) {
            configsDir.toFile().mkdirs();
        }

        try (FileReader reader = new FileReader(selectedFile)) {
            Genson genson = new Genson();
            Configuration config = genson.deserialize(reader, Configuration.class);

            String configName = config.getConfigName();

            if (configSelector.getItems().contains(configName)) {
                Alert duplicateAlert = new Alert(Alert.AlertType.ERROR);
                duplicateAlert.setTitle("Import Error");
                duplicateAlert.setHeaderText("Duplicate Configuration Name Detected");
                duplicateAlert.setContentText("A configuration with the name '" + configName + "' already exists.\nDuplicate configuration name is not allowed.");
                duplicateAlert.showAndWait();
                return;
            }

            File destFile = configsDir.resolve(configName + ".json").toFile();
            java.nio.file.Files.copy(selectedFile.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            StringBuilder failedFields = new StringBuilder();

            try {
                configNameField.setText(config.getConfigName());
            } catch (Exception e) {
                configNameField.clear();
                failedFields.append("Configuration Name\n");
            }

            try {
                progLangField.setText(config.getLanguage());
            } catch (Exception e) {
                progLangField.clear();
                failedFields.append("Programming Language\n");
            }

            try {
                executableExtensionField.setText(config.getExecutableExtension());
            } catch (Exception e) {
                executableExtensionField.clear();
                failedFields.append("Executable Extension\n");
            }

            try {
                compilerParametersField.setText(String.join(" ", config.getCompilerParameters()));
            } catch (Exception e) {
                compilerParametersField.clear();
                failedFields.append("Compiler Parameters\n");
            }


            try {
                runParametersField.setText(String.join(" ", config.getRunParameters()));
            } catch (Exception e) {
                runParametersField.clear();
                failedFields.append("Run Parameters\n");
            }
            try {
                if (config.getCompilerPath() == null || config.getCompilerPath().isEmpty()) {
                    compilerInstalledCheckBox.setSelected(true);
                    compilerPathField.clear();
                    compilerPathField.setVisible(false);
                    selectCompilerButton.setVisible(false);
                } else {
                    compilerInstalledCheckBox.setSelected(false);
                    compilerPathField.setText(config.getCompilerPath());
                    compilerPathField.setVisible(true);
                    selectCompilerButton.setVisible(true);
                }
            } catch (Exception e) {
                compilerInstalledCheckBox.setSelected(true);
                compilerPathField.clear();
                compilerPathField.setVisible(false);
                selectCompilerButton.setVisible(false);
                failedFields.append("Compiler Path\n");
            }


            configSelector.getItems().add(configName);
            configSelector.setValue(configName);


            if (failedFields.length() > 0) {
                Alert warning = new Alert(Alert.AlertType.WARNING);
                warning.setTitle("Import Completed with Warnings");
                warning.setHeaderText("Some fields could not be parsed:");
                warning.setContentText(failedFields.toString());
                warning.showAndWait();
            } else {
                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setHeaderText("Configuration imported successfully!");
                success.showAndWait();
            }

        } catch (IOException e) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText("Failed to import configuration.");
            error.setContentText(e.getMessage());
            error.showAndWait();
        } catch (Exception parseException) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText("Error while parsing the configuration file.");
            error.setContentText(parseException.getMessage());
            error.showAndWait();
        }
    }

    private void exportConfiguration() {
        String selectedConfig = configSelector.getValue();

        if (selectedConfig == null || selectedConfig.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("No configuration selected!");
            alert.setContentText("Please select a configuration to export.");
            alert.showAndWait();
            return;
        }

        Path configsDir = Paths.get(System.getProperty("user.dir"), "configs");
        File configFile = configsDir.resolve(selectedConfig + ".json").toFile();

        if (!configFile.exists()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Configuration file not found!");
            alert.setContentText("The selected configuration could not be found.");
            alert.showAndWait();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Configuration As");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        fileChooser.setInitialFileName(selectedConfig + ".json");

        File targetFile = fileChooser.showSaveDialog(stageReference);

        if (targetFile == null) {
            return;
        }

        try {
            java.nio.file.Files.copy(configFile.toPath(), targetFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setHeaderText("Export successful!");
            success.setContentText("Configuration exported to:\n" + targetFile.getAbsolutePath());
            success.showAndWait();
        } catch (IOException e) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText("Export failed!");
            error.setContentText(e.getMessage());
            error.showAndWait();
        }
    }

    private void clearFields() {
        configNameField.clear();
        progLangField.clear();
        executableExtensionField.clear();
        compilerParametersField.clear();
        runParametersField.clear();
        compilerInstalledCheckBox.setSelected(true);
        compilerPathField.clear();
        compilerPathField.setVisible(false);
        selectCompilerButton.setVisible(false);
    }

}
