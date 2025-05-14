package ce316project.views;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * A simple view component that displays user guide content using JavaFX controls.
 * This is a fallback for environments where WebView is not available.
 */
public class BasicUserGuideView extends BorderPane {
    
    /**
     * Constructs a new BasicUserGuideView that displays user guide content.
     */
    public BasicUserGuideView() {
        initializeUI();
    }
    
    /**
     * Initializes the UI components.
     */
    private void initializeUI() {
        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20));
        
        // Title
        Label titleLabel = new Label("CE-316 Project User Guide");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-border-color: #3498db; -fx-border-width: 0 0 2 0; -fx-padding: 0 0 5 0;");
        
        contentBox.getChildren().add(titleLabel);
        
        // Introduction Section
        addSection(contentBox, "Introduction", 
            "Welcome to the CE-316 Project, a tool designed to streamline the process of compiling, executing, " +
            "and comparing student submissions. This guide will help you understand how to use the application effectively.");
        
        // Main Interface Section
        addSection(contentBox, "Main Interface", 
            "The application consists of several components that help you manage student submissions and configurations.");
        
        addSubSection(contentBox, "Menu Bar");
        addFeature(contentBox, "Project Menu", 
            "Contains options for creating a new project and quitting the application.");
        addFeature(contentBox, "Configuration Menu", 
            "Allows you to create new configurations or open existing ones.");
        addFeature(contentBox, "Help Menu", 
            "Provides access to this user guide and information about the application.");
        
        // Project Workflow Section
        addSection(contentBox, "Project Workflow", "");
        
        addSubSection(contentBox, "Creating a New Project");
        Text workflowText = new Text(
            "To create a new project:\n" +
            "1. Click on Project > New Project or use shortcut Ctrl+N\n" +
            "2. Specify the project details including path to student submissions\n" +
            "3. Select or create a configuration for compilation and execution\n" +
            "4. Click \"Create\" to initialize the project"
        );
        contentBox.getChildren().add(workflowText);
        
        addSubSection(contentBox, "Managing Configurations");
        Text configText = new Text(
            "Configurations determine how submissions are compiled and executed.\n" +
            "• Create a new configuration via Configuration > New Configuration\n" +
            "• Open and edit existing configurations via Configuration > Open Configuration"
        );
        contentBox.getChildren().add(configText);
        
        // Features Section
        addSection(contentBox, "Features and Function Buttons", "");
        
        addSubSection(contentBox, "Compilation");
        Text compileText = new Text("The system supports both compiled and interpreted languages:");
        contentBox.getChildren().add(compileText);
        addFeature(contentBox, "Compile Submissions", 
            "Compiles all student submissions in parallel according to the configuration.");
        
        addSubSection(contentBox, "Execution");
        addFeature(contentBox, "Execute Submissions", 
            "Runs the compiled submissions and captures output and errors.");
        
        addSubSection(contentBox, "Comparison");
        addFeature(contentBox, "Compare Results", 
            "Compares student outputs against a reference solution to determine if they pass or fail.");
        
        // Status Indicators Section
        addSection(contentBox, "Status Indicators", 
            "Throughout the process, student submissions will have different status indicators:");
        
        Text statusText = new Text(
            "• READY: Successfully compiled and ready for execution\n" +
            "• COMPILING: Currently being compiled\n" +
            "• EXECUTING: Currently being executed\n" +
            "• COMPLETED: Execution completed successfully\n" +
            "• PASSED: Output matches reference solution\n" +
            "• FAILED: Output does not match reference solution\n" +
            "• ERROR: An error occurred during compilation or execution"
        );
        contentBox.getChildren().add(statusText);
        
        // Keyboard Shortcuts
        addSection(contentBox, "Keyboard Shortcuts", "");
        Text shortcutsText = new Text(
            "• Ctrl+N: Create New Project\n" +
            "• Ctrl+H: Open User Guide"
        );
        contentBox.getChildren().add(shortcutsText);
        
        // Troubleshooting
        addSection(contentBox, "Troubleshooting", 
            "If you encounter issues:");
        
        Text troubleshootingText = new Text(
            "• Check the error message in the compilation or execution results\n" +
            "• Verify that the configuration parameters match the requirements of the source code\n" +
            "• Ensure that compiler paths and execution commands are correctly specified\n" +
            "• For interpreted languages, make sure the interpreter path is correctly configured"
        );
        contentBox.getChildren().add(troubleshootingText);
        
        // Create scroll pane with content
        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(700);
        
        this.setCenter(scrollPane);
    }
    
    /**
     * Adds a section header and description to the content box.
     */
    private void addSection(VBox contentBox, String title, String description) {
        Label sectionTitle = new Label(title);
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        sectionTitle.setStyle("-fx-text-fill: #2980b9;");
        
        contentBox.getChildren().add(sectionTitle);
        
        if (!description.isEmpty()) {
            Text sectionText = new Text(description);
            sectionText.setWrappingWidth(850);
            contentBox.getChildren().add(sectionText);
        }
    }
    
    /**
     * Adds a subsection header to the content box.
     */
    private void addSubSection(VBox contentBox, String title) {
        Label subSectionTitle = new Label(title);
        subSectionTitle.setFont(Font.font("System", FontWeight.BOLD, 16));
        subSectionTitle.setStyle("-fx-text-fill: #3498db;");
        subSectionTitle.setPadding(new Insets(10, 0, 5, 0));
        
        contentBox.getChildren().add(subSectionTitle);
    }
    
    /**
     * Adds a feature with name and description.
     */
    private void addFeature(VBox contentBox, String name, String description) {
        BorderPane featurePane = new BorderPane();
        
        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        nameLabel.setPrefWidth(150);
        
        Text descriptionText = new Text(description);
        descriptionText.setWrappingWidth(700);
        
        featurePane.setLeft(nameLabel);
        featurePane.setCenter(descriptionText);
        
        contentBox.getChildren().add(featurePane);
    }
}