package ce316project.views;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;

public class AppMenuBar extends MenuBar {

    private Menu projectMenu = new Menu("Project");
    private Menu configMenu = new Menu("Configuration");
    private Menu helpMenu = new Menu("Help");

    private MenuItem mNewProject = new MenuItem("New Project");
    private MenuItem mQuit = new MenuItem("Quit");

    private MenuItem mNewConfig = new MenuItem("New Configuration");
    private MenuItem mOpenConfig = new MenuItem("Open Configuration");

    private MenuItem mUserGuide = new MenuItem("User Guide");

    private Stage primaryStage;

    public AppMenuBar(Stage primaryStage) {
        this.primaryStage = primaryStage;


        mNewProject.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
        mUserGuide.setAccelerator(KeyCombination.keyCombination("Ctrl+H"));

        mUserGuide.setOnAction(e -> openHelpMenuPage());
        mNewConfig.setOnAction(e -> openCreateConfigurationPage(primaryStage));
        mOpenConfig.setOnAction(e -> openEditConfigurationPage(primaryStage));
        mNewProject.setOnAction(e -> openCreateProjectPage(primaryStage));
        mQuit.setOnAction(e -> {System.exit(0);});

        projectMenu.getItems().addAll(mNewProject, new SeparatorMenuItem(), mQuit);
        configMenu.getItems().addAll(mNewConfig, new SeparatorMenuItem() ,mOpenConfig);
        helpMenu.getItems().addAll(mUserGuide);

        this.getMenus().addAll(projectMenu, configMenu, helpMenu);
    }

    private void openCreateConfigurationPage(Stage primaryStage) {
        CreateConfigurationPage createPage = new CreateConfigurationPage(primaryStage, null);

        Stage popupStage = new Stage();
        popupStage.setTitle("Create New Configuration");
        popupStage.initOwner(primaryStage);
        popupStage.setScene(new Scene(createPage, 500, 700));
        popupStage.show();
    }

    private void openEditConfigurationPage(Stage primaryStage) {
        EditConfigurationPage openPage = new EditConfigurationPage(primaryStage);

        Stage popupStage = new Stage();
        popupStage.setTitle("Edit Existing Configuration");
        popupStage.initOwner(primaryStage);
        popupStage.setScene(new Scene(openPage, 600, 800));
        popupStage.show();
    }

    private void openCreateProjectPage(Stage primaryStage) {
        CreateProjectPage createProjectPage = new CreateProjectPage(primaryStage);

        Stage popupStage = new Stage();
        popupStage.setTitle("Create New Project");
        popupStage.initOwner(primaryStage);
        popupStage.setScene(new Scene(createProjectPage, 500, 500));
        popupStage.show();
    }

    private void openHelpMenuPage() {
        HelpGuide helpMenuPage = new HelpGuide();

        Stage popupStage = new Stage();
        popupStage.setTitle("Help");
        popupStage.initOwner(primaryStage);
        popupStage.setScene(new Scene(helpMenuPage, 600, 500));
        popupStage.show();
    }
}
