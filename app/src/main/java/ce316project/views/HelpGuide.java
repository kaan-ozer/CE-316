package ce316project.views;

import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class HelpGuide extends ScrollPane {

    public HelpGuide() {
        // Configure the scroll pane
        this.setFitToWidth(true);
        this.setPadding(new Insets(0));
        
        // Apply CSS to this ScrollPane
        this.getStyleClass().add("help-guide");
        
        // Main container
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(25));
        mainContent.getStyleClass().add("help-content");
        
        // Title
        Label title = new Label("User Guide - Integrated Assignment Environment");
        title.getStyleClass().add("help-title");
        
        // Create accordion for collapsible sections
        Accordion helpSections = new Accordion();
        helpSections.getStyleClass().add("help-accordion");
        
        // Getting Started Section
        helpSections.getPanes().add(createHelpSection("Getting Started",
                "The Integrated Assignment Environment helps you manage and evaluate student programming " +
                "assignments. Start by creating a programming language configuration, then create " +
                "a project that uses that configuration."));
        
        // Configurations Section
        helpSections.getPanes().add(createHelpSection("Managing Configurations", 
                "Configurations define how to compile and run programs in a specific programming language.\n\n" +
                "To create a new configuration:\n" +
                "1. Click on 'Configuration' > 'New Configuration'\n" +
                "2. Enter a name for your configuration\n" +
                "3. Specify the programming language (e.g., Java, C++, Python)\n" +
                "4. Set the file extensions for source and executable files\n" +
                "5. Add any compiler/run parameters required\n" +
                "6. If the compiler isn't in your system PATH, uncheck the checkbox and select the compiler executable\n" +
                "7. Click 'Generate Configuration'\n\n" +
                "To edit existing configurations, go to 'Configuration' > 'Open Configuration'"));
        
        // Projects Section
        helpSections.getPanes().add(createHelpSection("Creating Projects",
                "Projects organize student submissions for a specific assignment.\n\n" +
                "To create a project:\n" +
                "1. Click on 'Project' > 'New Project'\n" +
                "2. Enter a project name\n" +
                "3. Select a configuration for the programming language\n" +
                "4. Browse to select the folder containing student submissions\n" +
                "5. Browse to select the expected output file\n" +
                "6. Click 'Create Project'\n\n" +
                "After creating a project, refresh the main page to see it in the project list."));
        
        // Evaluating Submissions Section
        helpSections.getPanes().add(createHelpSection("Evaluating Submissions",
                "To evaluate student submissions:\n\n" +
                "1. Select a project from the dropdown on the main page\n" +
                "2. Click the 'RUN' button to compile and run all submissions\n" +
                "3. Click 'Show Results' to see the results in the table\n\n" +
                "The table shows:\n" +
                "• Student ID\n" +
                "• Result status (SUCCESS/FAIL)\n" +
                "• Directory\n" +
                "• Standard output\n" +
                "• Standard error (if any)\n" +
                "• Execution duration"));
        
        // Exporting Results Section
        helpSections.getPanes().add(createHelpSection("Exporting Results",
                "After running a project, you can export the results:\n\n" +
                "1. Click the 'Export Results' button\n" +
                "2. Choose a location to save the text file\n" +
                "3. The exported file will contain all student results in a tabular format"));
        
        // Managing Projects Section
        helpSections.getPanes().add(createHelpSection("Managing Projects",
                "• To switch between projects, select from the dropdown menu\n" +
                "• Click 'Refresh' after creating a new project\n" +
                "• The 'Delete Project' button removes the selected project"));

        // Add all components to main container
        mainContent.getChildren().addAll(title, helpSections);
        
        // Set the content for the scroll pane
        this.setContent(mainContent);
        
        // Listen for when the scene is available and then apply styles
        this.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                applyStyles(newValue);
            }
        });
    }
    
    private TitledPane createHelpSection(String title, String content) {
        VBox sectionContent = new VBox(10);
        sectionContent.setPadding(new Insets(15));
        sectionContent.getStyleClass().add("help-section");
        
        Text text = new Text(content);
        text.setWrappingWidth(580);
        text.getStyleClass().add("help-text");
        
        TextFlow textFlow = new TextFlow(text);
        sectionContent.getChildren().add(textFlow);
        
        TitledPane section = new TitledPane(title, sectionContent);
        section.getStyleClass().add("help-titled-pane");
        return section;
    }
    
    private void applyStyles(javafx.scene.Scene scene) {
        // Define CSS styles as a string
        String css = 
            ".help-guide { -fx-background-color: #f0f5fa; }" +
            
            ".help-content { -fx-background-color: white; " +
            "               -fx-background-radius: 8px; " +
            "               -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 2); }" +
            
            ".help-title { -fx-font-family: 'Segoe UI', Arial, sans-serif; " +
            "             -fx-font-size: 22px; " +
            "             -fx-font-weight: bold; " +
            "             -fx-text-fill: #2c3e50; " +
            "             -fx-padding: 0 0 15 0; " +
            "             -fx-border-style: hidden hidden solid hidden; " +
            "             -fx-border-width: 0 0 1 0; " +
            "             -fx-border-color: #e0e0e0; }" +
            
            ".help-accordion { -fx-background-color: transparent; }" +
            
            ".help-accordion .titled-pane { -fx-text-fill: #2c3e50; }" +
            
            ".help-accordion .titled-pane > .title { -fx-background-color: #ecf0f1; " +
            "                                       -fx-font-weight: bold; " +
            "                                       -fx-font-size: 14px; " +
            "                                       -fx-padding: 12px; " +
            "                                       -fx-background-radius: 4px; }" +
            
            ".help-accordion .titled-pane:expanded > .title { -fx-background-color: #3498db; " +
            "                                               -fx-text-fill: white; }" +
            
            ".help-accordion .titled-pane > .title:hover { -fx-background-color: #bdc3c7; }" +
            
            ".help-accordion .titled-pane:expanded > .title:hover { -fx-background-color: #2980b9; }" +
            
            ".help-accordion .titled-pane > .content { -fx-background-color: white; " +
            "                                         -fx-border-color: #e0e0e0; " +
            "                                         -fx-border-width: 1; " +
            "                                         -fx-border-radius: 0 0 4px 4px; }" +
            
            ".help-section { -fx-background-color: white; }" +
            
            ".help-text { -fx-font-family: 'Segoe UI', Arial, sans-serif; " +
            "            -fx-font-size: 14px; " +
            "            -fx-line-spacing: 5px; " +
            "            -fx-fill: #34495e; }";
        
        // Add the stylesheet to the scene
        scene.getStylesheets().add("data:text/css," + css.replace(" ", "%20"));
    }
}
