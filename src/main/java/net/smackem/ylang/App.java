package net.smackem.ylang;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {
    private static App INSTANCE;
    private Scene scene;

    public App() {
        if (INSTANCE != null) {
            throw new RuntimeException("only one instance allowed!");
        }
        INSTANCE = this;
    }

    public static App getInstance() {
        return INSTANCE;
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.scene = new Scene(loadFXML("primary"), 640, 480);
        stage.setScene(this.scene);
        stage.show();
    }

    void setRoot(String fxml) throws IOException {
        this.scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        final FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}