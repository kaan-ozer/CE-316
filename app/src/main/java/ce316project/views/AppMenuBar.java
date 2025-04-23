package ce316project.views;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;

public class AppMenuBar extends MenuBar {

    private Menu projectMenu = new Menu("Project");
    private Menu configMenu = new Menu("Configuration");
    private Menu helpMenu = new Menu("Help");

    private MenuItem mNewProject = new MenuItem("New Project");  // Ctrl+N

    private MenuItem mQuit = new MenuItem("Quit");

    private MenuItem mNewConfig = new MenuItem("New Configuration");
    private MenuItem mImportConfig = new MenuItem("Import Config");
    private MenuItem mExportConfig = new MenuItem("Export Config");

    private MenuItem mUserGuide = new MenuItem("User Guide");   // Ctrl+H
    private MenuItem mAbout = new MenuItem("About");

    public AppMenuBar() {

        mNewProject.setAccelerator(KeyCombination.keyCombination("Ctrl+N"));
        mUserGuide.setAccelerator(KeyCombination.keyCombination("Ctrl+H"));

        mQuit.setOnAction(e -> quit());
        mUserGuide.setOnAction(e -> showUserGuide());
        mAbout.setOnAction(e -> showAboutInfo());

        projectMenu.getItems().addAll(mNewProject, new SeparatorMenuItem(), mQuit);

        mNewConfig.setOnAction(e -> System.out.println("Open Configuration Manager"));
        mImportConfig.setOnAction(e -> System.out.println("Import Config"));
        mExportConfig.setOnAction(e -> System.out.println("Export Config"));

        configMenu.getItems().addAll(mNewConfig, mImportConfig, mExportConfig);

        helpMenu.getItems().addAll(mUserGuide, new SeparatorMenuItem(), mAbout);


        this.getMenus().addAll(projectMenu, configMenu, helpMenu);
    }


    private void showUserGuide() {
        try {
            File userGuide = new File("shared/userGuide.pdf");
            if (userGuide.exists()) {
                Desktop.getDesktop().open(userGuide);
            } else {
                System.out.println("User guide not found!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAboutInfo() {
        JOptionPane.showMessageDialog(null, "YourApp v1.0\nDeveloped by My G.", "About YourApp",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void quit() {
        System.exit(0);
    }
}