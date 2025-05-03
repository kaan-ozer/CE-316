package ce316project.views;

import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import java.net.URL;
import java.util.Objects;
import ce316project.utils.ResourceLoader;

/**
 * A view component that displays the user guide HTML content using JavaFX WebView.
 */
public class UserGuideView extends BorderPane {
    
    private WebView webView;
    private WebEngine webEngine;
    
    /**
     * Constructs a new UserGuideView that loads and displays the User Guide HTML content.
     */
    public UserGuideView() {
        initializeUI();
        loadUserGuide();
    }
    
    /**
     * Initializes the UI components.
     */
    private void initializeUI() {
        webView = new WebView();
        webEngine = webView.getEngine();
        webView.setPrefSize(900, 700);
        
        // Set WebView as the center component of the BorderPane
        this.setCenter(webView);
    }
    
    /**
     * Loads the User Guide HTML content into the WebView.
     */
    private void loadUserGuide() {
        try {
            // Get reference to the HTML file in resources folder using our utility
            URL userGuideURL = ResourceLoader.getResourceURL("UserGuide.html");
            
            if (userGuideURL != null) {
                webEngine.load(userGuideURL.toExternalForm());
            } else {
                // If the guide file can't be found, show an error message with troubleshooting info
                webEngine.loadContent(
                    "<html><body style='font-family: Arial, sans-serif; margin: 20px;'>" +
                    "<h1 style='color: #cc0000;'>Error Loading User Guide</h1>" +
                    "<p>The user guide file could not be found in the resources directory.</p>" +
                    "<h3>Troubleshooting:</h3>" +
                    "<ol>" +
                    "<li>Ensure the file 'UserGuide.html' exists in the resources directory</li>" +
                    "<li>Verify that the resources directory is correctly included in the build</li>" +
                    "<li>The file path should be 'app/src/main/resources/UserGuide.html'</li>" +
                    "</ol>" +
                    "</body></html>"
                );
            }
        } catch (Exception e) {
            // Show error message if loading fails
            webEngine.loadContent(
                "<html><body style='font-family: Arial, sans-serif; margin: 20px;'>" +
                "<h1 style='color: #cc0000;'>Error Loading User Guide</h1>" +
                "<p>An error occurred while loading the user guide: " + e.getMessage() + "</p>" +
                "<h3>Stack Trace:</h3>" +
                "<pre style='background-color: #f5f5f5; padding: 10px; overflow: auto;'>" + getStackTraceAsString(e) + "</pre>" +
                "</body></html>"
            );
        }
    }
    
    /**
     * Converts a stack trace to a string.
     * 
     * @param e The exception to convert
     * @return The stack trace as a string
     */
    private String getStackTraceAsString(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
} 