package ce316project.views;

import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import java.net.URL;
import java.util.Objects;

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
            // Get reference to the HTML file in resources folder
            URL userGuideURL = getClass().getClassLoader().getResource("UserGuide.html");

            if (userGuideURL != null) {
                webEngine.load(userGuideURL.toExternalForm());
            } else {
                // If the guide file can't be found, show an error message
                webEngine.loadContent(
                    "<html><body>" +
                    "<h1>Error Loading User Guide</h1>" +
                    "<p>The user guide file could not be found in the resources directory.</p>" +
                    "</body></html>"
                );
            }
        } catch (Exception e) {
            // Show error message if loading fails
            webEngine.loadContent(
                "<html><body>" +
                "<h1>Error Loading User Guide</h1>" +
                "<p>An error occurred while loading the user guide: " + e.getMessage() + "</p>" +
                "</body></html>"
            );
        }
    }
}