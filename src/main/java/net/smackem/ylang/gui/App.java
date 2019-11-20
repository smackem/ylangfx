package net.smackem.ylang.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * JavaFX App
 */
public class App extends Application {
    private static App INSTANCE;
    private Scene scene;
    private Stage stage;

    public App() {
        if (INSTANCE != null) {
            throw new RuntimeException("only one instance allowed!");
        }
        INSTANCE = this;
    }

    public static App getInstance() {
        return INSTANCE;
    }

    public Stage getStage() {
        return this.stage;
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.scene = new Scene(loadFXML("imageproc"), 640, 480);
        this.stage = stage;
        stage.setScene(this.scene);
        stage.show();
    }

    void setRoot(String fxml) throws IOException {
        this.scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        final URL fxmlUrl = App.class.getResource(fxml + ".fxml");
        final FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}