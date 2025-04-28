package ce316project.views;

import ce316project.entities.Configuration;
import ce316project.entities.Project;
import com.owlike.genson.Genson;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
import java.util.Date;

public class CreateProjectPage extends VBox {

    private Stage stageReference;
    private TextField projectNameField;
    private ComboBox<String> configSelector;
    private Button newConfigButton;
    private Button importConfigButton;
    private TextField submissionZipField;
    private Button browseZipButton;
    private TextField expectedOutputField;
    private Button browseOutputButton;
    private Button generateButton;

    private Configuration selectedConfiguration = null;

    public CreateProjectPage(Stage stageReference) {
        this.stageReference = stageReference;

        this.setPadding(new Insets(20));
        this.setSpacing(15);
        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle("-fx-background-color: #D3D3D3;");

        projectNameField = new TextField();
        projectNameField.setPromptText("Project Name");

        configSelector = new ComboBox<>();
        configSelector.setPromptText("Select Configuration");
        loadConfigurationFiles();

        configSelector.setOnAction(e -> {
            String selectedConfigName = configSelector.getValue();
            if (selectedConfigName != null) {
                selectedConfiguration = loadConfiguration(selectedConfigName);
            }
        });

        newConfigButton = new Button("New");
        newConfigButton.setOnAction(e -> openNewConfigurationPage());

        importConfigButton = new Button("Import");
        importConfigButton.setOnAction(e -> importConfiguration());

        HBox configBox = new HBox(10, configSelector, newConfigButton, importConfigButton);
        configBox.setAlignment(Pos.CENTER);

        submissionZipField = new TextField();
        submissionZipField.setPromptText("Submission Zip File");
        submissionZipField.setEditable(false);

        browseZipButton = new Button("Browse");
        browseZipButton.setOnAction(e -> selectSubmissionZip());

        HBox submissionBox = new HBox(10, submissionZipField, browseZipButton);
        submissionBox.setAlignment(Pos.CENTER);

        expectedOutputField = new TextField();
        expectedOutputField.setPromptText("Expected Output File");
        expectedOutputField.setEditable(false);

        browseOutputButton = new Button("Browse");
        browseOutputButton.setOnAction(e -> selectExpectedOutput());

        HBox outputBox = new HBox(10, expectedOutputField, browseOutputButton);
        outputBox.setAlignment(Pos.CENTER);

        generateButton = new Button("Save Project");
        generateButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-size: 14px;");
        generateButton.setOnAction(e -> generateProject());

        this.getChildren().addAll(
                new Label("Project Name:"), projectNameField,
                new Label("Configuration:"), configBox,
                new Label("Submission Zip File:"), submissionBox,
                new Label("Expected Output File:"), outputBox,
                generateButton
        );
    }

    private void loadConfigurationFiles() {
        Path configsDir = Paths.get(System.getProperty("user.dir"), "src", "main", "java", "ce316project", "configs");
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

    private Configuration loadConfiguration(String configName) {
        Path configsDir = Paths.get(System.getProperty("user.dir"), "src", "main", "java", "ce316project", "configs");
        File configFile = configsDir.resolve(configName + ".json").toFile();

        try (FileReader reader = new FileReader(configFile)) {
            Genson genson = new Genson();
            return genson.deserialize(reader, Configuration.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void openNewConfigurationPage() {
        CreateConfigurationPage editPage = new CreateConfigurationPage(stageReference, () -> {
            loadConfigurationFiles();
        });
        Stage editStage = new Stage();
        editStage.setScene(new Scene(editPage));
        editStage.setTitle("Create/Edit Configuration");
        editStage.showAndWait();
    }

    private void selectSubmissionZip() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Submission Zip File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ZIP Files", "*.zip"));
        File selectedFile = fileChooser.showOpenDialog(stageReference);
        if (selectedFile != null) {
            submissionZipField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void selectExpectedOutput() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Expected Output File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File selectedFile = fileChooser.showOpenDialog(stageReference);
        if (selectedFile != null) {
            expectedOutputField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void importConfiguration() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import Configuration File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));

        File selectedFile = fileChooser.showOpenDialog(stageReference);
        if (selectedFile == null) {
            return;
        }

        Path configsDir = Paths.get(System.getProperty("user.dir"), "src", "main", "java", "ce316project", "configs");

        if (!configsDir.toFile().exists()) {
            configsDir.toFile().mkdirs();
        }

        try (FileReader reader = new FileReader(selectedFile)) {
            Genson genson = new Genson();
            Configuration importedConfig = genson.deserialize(reader, Configuration.class);
            String importedConfigName = importedConfig.getConfigName();

            File[] existingConfigs = configsDir.toFile().listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
            if (existingConfigs != null) {
                for (File file : existingConfigs) {
                    String existingName = file.getName().replace(".json", "");
                    if (existingName.equals(importedConfigName)) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Duplicate Configuration Name!");
                        alert.setContentText("A configuration with the name '" + importedConfigName + "' already exists.\nImport cancelled.");
                        alert.showAndWait();
                        return;
                    }
                }
            }


            File destFile = configsDir.resolve(importedConfigName + ".json").toFile();
            java.nio.file.Files.copy(selectedFile.toPath(), destFile.toPath());

            configSelector.getItems().clear();
            loadConfigurationFiles();

            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setHeaderText("Configuration Imported Successfully!");
            success.setContentText("Imported configuration: " + importedConfigName);
            success.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void generateProject() {
        String projectName = projectNameField.getText().trim();
        String submissionZipPath = submissionZipField.getText().trim();
        String expectedOutputPath = expectedOutputField.getText().trim();

        StringBuilder missingFields = new StringBuilder();

        if (projectName.isEmpty()) missingFields.append("Project Name\n");
        if (selectedConfiguration == null) missingFields.append("Configuration\n");
        if (submissionZipPath.isEmpty()) missingFields.append("Submission Zip File\n");
        if (expectedOutputPath.isEmpty()) missingFields.append("Expected Output File\n");

        if (missingFields.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Missing Fields!");
            alert.setContentText(missingFields.toString());
            alert.showAndWait();
            return;
        }

        Project project = new Project(
                null,
                projectName,
                selectedConfiguration,
                null,
                null,
                expectedOutputPath
        );

        project.setCreationDate(new Date());

        saveProjectToJson(project);

        clearForm();

        Alert success = new Alert(Alert.AlertType.INFORMATION);
        success.setHeaderText("Project Created and Saved Successfully!");
        success.setContentText("Project Name: " + projectName);
        success.showAndWait();
    }

    private void saveProjectToJson(Project project) {
        Path projectsDir = Paths.get(System.getProperty("user.dir"), "src", "main", "java", "ce316project", "projects");

        if (!projectsDir.toFile().exists()) {
            projectsDir.toFile().mkdirs();
        }

        File projectFile = projectsDir.resolve(project.getProjectName() + ".json").toFile();

        try (FileWriter writer = new FileWriter(projectFile)) {
            writer.write("{\n");
            writer.write("  \"Project Name\": \"" + project.getProjectName() + "\",\n");
            writer.write("  \"Configuration Name\": \"" + (project.getConfig() != null ? project.getConfig().getConfigName() : "") + "\",\n");
            writer.write("  \"Zip File Path\": \"" + submissionZipField.getText().trim() + "\",\n");
            writer.write("  \"Expected Output File Path\": \"" + project.getReferencePath() + "\"\n");
            writer.write("}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearForm() {
        projectNameField.clear();
        configSelector.setValue(null);
        selectedConfiguration = null;
        submissionZipField.clear();
        expectedOutputField.clear();
    }
}