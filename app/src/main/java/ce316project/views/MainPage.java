package ce316project.views;

import java.util.HashMap;

import ce316project.controller.PageController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class MainPage extends VBox {

    private AppMenuBar menuBar = new AppMenuBar();

    private ComboBox<String> projectSelector = new ComboBox<>();
    private Button refreshButton = createIconButton("Refresh", "icons/refresh.png");
    private Button runButton = createIconButton("RUN", "icons/run.png");
    private Button showResultsButton = createIconButton("Show Results", "icons/results.png");
    private Button zipConvertButton = createIconButton("Convert to ZIP file", "icons/zip.png");

    private TableView<String> resultTable = new TableView<>();

    public MainPage() {
        // Title area
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

        // Top control panel
        Label selectLabel = new Label("Select Project:");
        selectLabel.setStyle("-fx-text-fill: #CCCCCC;");
        HBox controlPanel = new HBox(10, selectLabel, projectSelector, refreshButton);
        controlPanel.setAlignment(Pos.CENTER_LEFT);
        controlPanel.setPadding(new Insets(10));

        // Table setup
        TableColumn<String, String> studentCol = new TableColumn<>("Student ID");
        TableColumn<String, String> resultCol = new TableColumn<>("Result");
        resultTable.getColumns().addAll(studentCol, resultCol);
        resultTable.setPlaceholder(new Label("No submissions yet."));
        resultTable.setPrefHeight(250);
        resultTable.setStyle("-fx-background-color: #2A2A2E; -fx-text-fill: #F0F0F0;");

        // Action buttons
        HBox actions = new HBox(20, runButton, showResultsButton, zipConvertButton);
        actions.setAlignment(Pos.CENTER);
        actions.setPadding(new Insets(15));
        actions.setStyle("-fx-background-color: #2F3136;");

        // Footer
        Label footer = new Label("Department of Computer Engineering - CE316 Project");
        footer.setStyle("-fx-text-fill: #AAAAAA; -fx-font-weight: bold;");
        footer.setPadding(new Insets(10));
        footer.setAlignment(Pos.CENTER);

        // Main layout
        VBox mainLayout = new VBox(10, controlPanel, resultTable, actions, footer);
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setPadding(new Insets(10));
        mainLayout.setStyle("-fx-background-color: #1E1F22; -fx-text-fill: white;");

        this.getChildren().addAll(menuBar, header, mainLayout);
        this.setStyle("-fx-background-color: #0F0F10;");


        if (!PageController.pagesArray.isEmpty()) {
            PageController.pagesArray.add(PageController.pagesArray.size(), PageController.pagesArray.get(PageController.pagesArray.size() - 1));
        }
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

    public void fillCheckLists(HashMap<String, Integer> hashmap, TitledPane checkBoxList, String type) {
        VBox checkBoxVBox = new VBox();
        ScrollPane checkBoxScrollPane = new ScrollPane(checkBoxVBox);
        checkBoxScrollPane.setMaxHeight(800);
        checkBoxScrollPane.setMaxWidth(120);

        if (hashmap.isEmpty()) {
            checkBoxList.setExpanded(false);
        }

        for (String key : hashmap.keySet()) {
            HBox checkBoxItemBox = new HBox();
            CheckBox checkBox = new CheckBox(key);
            Label countLabel = new Label(hashmap.get(key).toString().trim());
            checkBoxItemBox.getChildren().addAll(checkBox, countLabel);
            checkBoxItemBox.setSpacing(5);
            checkBoxItemBox.setPadding(new Insets(0, 0, 5, 5));
            checkBox.setStyle("-fx-text-fill: #F0F0F0;");
            countLabel.setStyle("-fx-text-fill: #F0F0F0;");
            checkBoxVBox.getChildren().add(checkBoxItemBox);
        }

        checkBoxList.setContent(checkBoxScrollPane);
    }
}