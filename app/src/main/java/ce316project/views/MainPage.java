package ce316project.views;

import ce316project.entities.Project;
import ce316project.entities.Student;
import com.owlike.genson.Genson;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class MainPage extends VBox {

    private AppMenuBar menuBar;
    private ComboBox<String> projectSelector = new ComboBox<>();
    private Button refreshButton = createIconButton("Refresh", "icons/refresh.png");
    private Button runButton = createIconButton("RUN", "icons/run.png");
    private Button showResultsButton = createIconButton("Show Results", "icons/results.png");
    private Button deleteProjectButton = createIconButton("Delete Project", "icons/delete.png");

    private TableView<Student> resultTable = new TableView<>();

    private Project currentProject;
    private boolean runExecuted = false;

    public MainPage(Stage primaryStage) {
        menuBar = new AppMenuBar(primaryStage);

        VBox header = new VBox(5);
        Label title = new Label("Integrated Assignment Environment");
        title.setFont(Font.font("Arial", 20));
        title.setStyle("-fx-text-fill: #ECECEC;");
        Label subtitle = new Label("Manage and evaluate student programming assignments easily");
        subtitle.setFont(Font.font("Arial", 12));
        subtitle.setStyle("-fx-text-fill: #BBBBBB;");
        header.getChildren().addAll(title, subtitle);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(15));

        Label selectLabel = new Label("Select Project:");
        selectLabel.setStyle("-fx-text-fill: #CCCCCC;");
        projectSelector.setPromptText("-- Select a project --");
        projectSelector.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "-- Select a project --" : item);
            }
        });

        HBox controlPanel = new HBox(10, selectLabel, projectSelector, refreshButton);
        controlPanel.setAlignment(Pos.CENTER_LEFT);
        controlPanel.setPadding(new Insets(10));

        TableColumn<Student, String> studentCol = new TableColumn<>("Student ID");
        studentCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStudentId()));
        TableColumn<Student, String> resultCol = new TableColumn<>("Result");
        resultCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus().toString()));
        resultTable.getColumns().addAll(studentCol, resultCol);
        resultTable.setPlaceholder(new Label("No submissions yet."));
        resultTable.setPrefHeight(250);
        resultTable.setStyle("-fx-background-color: #2A2A2E; -fx-text-fill: #F0F0F0;");

        runButton.setDisable(true);
        showResultsButton.setDisable(true);
        HBox actions = new HBox(20, runButton, showResultsButton, deleteProjectButton);

        actions.setAlignment(Pos.CENTER);
        actions.setPadding(new Insets(15));
        actions.setStyle("-fx-background-color: #2F3136;");

        Label footer = new Label("Department of Computer Engineering - CE316 Project");
        footer.setStyle("-fx-text-fill: #AAAAAA; -fx-font-weight: bold;");
        footer.setPadding(new Insets(10));
        footer.setAlignment(Pos.CENTER);

        VBox mainLayout = new VBox(10, controlPanel, resultTable, actions, footer);
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setPadding(new Insets(10));
        mainLayout.setStyle("-fx-background-color: #1E1F22; -fx-text-fill: white;");
        this.getChildren().addAll(menuBar, header, mainLayout);
        this.setStyle("-fx-background-color: #0F0F10;");

        loadProjects();

        refreshButton.setOnAction(e -> {
            loadProjects();
            projectSelector.setValue(null);
            projectSelector.setPromptText("-- Select a project --");
            runButton.setDisable(true);
            showResultsButton.setDisable(true);
            resultTable.getItems().clear();
        });

        projectSelector.setOnAction(e -> {
            String selected = projectSelector.getValue();
            if (selected != null) {
                loadProject(selected);
                runButton.setDisable(false);
                runExecuted = false;
                showResultsButton.setDisable(true);
            }
        });

        runButton.setOnAction(e -> {
            if (currentProject != null) {
                currentProject.compileSubmissions();
                currentProject.runSubmissions(currentProject.getExpectedOutputPath());
                showAlert("Run Complete", "Execution completed for project: " + currentProject.getProjectName());
                runExecuted = true;
                showResultsButton.setDisable(false);
            } else {
                showAlert("No Project Selected", "Please select a project first.");
            }
        });

        showResultsButton.setOnAction(e -> {
            if (!runExecuted) {
                showAlert("Execution Required", "Please run the project before viewing results.");
                return;
            }
            if (currentProject != null && currentProject.getStudents() != null) {
                resultTable.getItems().setAll(currentProject.getStudents());
            } else {
                showAlert("No Results", "No execution data available. Run the project first.");
            }
        });

        deleteProjectButton.setOnAction(e -> {
            String selected = projectSelector.getValue();
            if (selected == null) {
                showAlert("No Project Selected", "Please select a project to delete.");
                return;
            }

            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
            confirmation.setTitle("Confirm Deletion");
            confirmation.setHeaderText("Are you sure you want to delete this project?");
            confirmation.setContentText("Project: " + selected);

            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    deleteSelectedProject(selected);
                }
            });
        });
    }

    private void deleteSelectedProject(String projectName) {
        Path projectsDir = Paths.get(System.getProperty("user.dir"), "src", "main", "java", "ce316project", "projects");
        File projectFile = projectsDir.resolve(projectName + ".json").toFile();

        if (projectFile.exists() && projectFile.delete()) {
            projectSelector.getItems().remove(projectName);
            projectSelector.setValue(null);
            resultTable.getItems().clear();
            runButton.setDisable(true);
            showResultsButton.setDisable(true);
            currentProject = null;
            showAlert("Project Deleted", "Project '" + projectName + "' was deleted successfully.");
        } else {
            showAlert("Error", "Failed to delete project: " + projectName);
        }
    }

    private void loadProjects() {
        projectSelector.getItems().clear();
        projectSelector.setValue(null);
        projectSelector.setPromptText("-- Select a project --");
        projectSelector.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "-- Select a project --" : item);
            }
        });
        Path projectsDir = Paths.get(System.getProperty("user.dir"), "src", "main", "java", "ce316project", "projects");
        if (Files.exists(projectsDir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(projectsDir, "*.json")) {
                for (Path p : stream) {
                    projectSelector.getItems().add(p.getFileName().toString().replace(".json", ""));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadProject(String projectName) {
        try {
            Path projectsDir = Paths.get(System.getProperty("user.dir"), "src", "main", "java", "ce316project", "projects");
            File projectFile = projectsDir.resolve(projectName + ".json").toFile();
            Genson genson = new Genson();
            currentProject = genson.deserialize(new FileReader(projectFile), Project.class);
            currentProject.setStudents(new ArrayList<>());
            currentProject.prepareSubmissions(currentProject.getSubmissionsPath());
        } catch (IOException e) {
            showAlert("Error Loading Project", e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private Button createIconButton(String text, String iconPath) {
        Button button;
        try {
            ImageView iconView = new ImageView(new Image(getClass().getResourceAsStream("/" + iconPath)));
            iconView.setFitWidth(20);
            iconView.setFitHeight(20);
            button = new Button(text, iconView);
        } catch (Exception e) {
            button = new Button(text);
        }
        button.setStyle("-fx-font-size: 13px; -fx-content-display: LEFT; -fx-background-color: #3C3F41; -fx-text-fill: #FFFFFF; -fx-padding: 8px 12px; -fx-background-radius: 6px;");
        return button;
    }
}