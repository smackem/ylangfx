package net.smackem.ylang.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.smackem.ylang.model.RemoteImageProcService;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.prefs.Preferences;

/**
 * JavaFX App
 */
public final class App extends Application {
    private static final String PREF_WIDTH = "mainStage.width";
    private static final String PREF_HEIGHT = "mainStage.height";
    private static final String PREF_LEFT = "mainStage.left";
    private static final String PREF_TOP = "mainStage.top";
    public static final Executor UI_EXECUTOR = runnable -> {
        if (Platform.isFxApplicationThread()) {
            runnable.run();
        } else {
            Platform.runLater(runnable);
        }
    };
    private Scene scene;
    private Stage stage;
    private RemoteImageProcService imageProcService;
    private static App INSTANCE;

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

    public RemoteImageProcService getImageProcService() {
        return this.imageProcService;
    }

    @Override
    public void start(Stage stage) throws IOException {
        final Preferences prefs = Preferences.userNodeForPackage(App.class);
        final double width = prefs.getDouble(PREF_WIDTH, 800);
        final double height = prefs.getDouble(PREF_HEIGHT, 600);
        final double x = prefs.getDouble(PREF_LEFT, Double.NaN);
        final double y = prefs.getDouble(PREF_TOP, Double.NaN);
        this.scene = new Scene(loadFXML("imageproc"), width, height);
        this.stage = stage;
        if (Double.isNaN(x) == false && Double.isNaN(y) == false) {
            stage.setX(x);
            stage.setY(y);
        }
        this.imageProcService = new RemoteImageProcService("localhost", 50051, UI_EXECUTOR);
        stage.setScene(this.scene);
        stage.show();
        stage.setOnCloseRequest(ignored -> {
            prefs.putDouble(PREF_WIDTH, stage.getWidth());
            prefs.putDouble(PREF_HEIGHT, stage.getHeight());
            prefs.putDouble(PREF_LEFT, stage.getX());
            prefs.putDouble(PREF_TOP, stage.getY());
        });
    }

    @Override
    public void stop() throws Exception {
        if (this.imageProcService != null) {
            this.imageProcService.close();
        }
        super.stop();
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