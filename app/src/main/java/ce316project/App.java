package ce316project;

import ce316project.controller.PageController;
import ce316project.views.MainPage;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        MainPage mainPage = new MainPage(primaryStage);
        Scene scene = new Scene(mainPage, 800, 450);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Integrated Assignment Environment");
        PageController.pagesArray.add(primaryStage);
        primaryStage.show();

    }
}
