package net.smackem.ylang.gui;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import net.smackem.ylang.model.ProcessImageResult;
import net.smackem.ylang.model.RemoteImageProcService;

import javax.imageio.ImageIO;

public class ImageProcController {

    private final ReadOnlyObjectWrapper<Image> sourceImage = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<Image> targetImage = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyStringWrapper message = new ReadOnlyStringWrapper();
    private final RemoteImageProcService imageProcService;
    private static final KeyCombination KEY_COMBINATION_RUN = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN);
    private static final KeyCombination KEY_COMBINATION_TAKE = new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN);
    private static final KeyCombination KEY_COMBINATION_SAVEAS = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);

    @FXML
    private Tab targetTab;

    @FXML
    private Tab sourceTab;

    @FXML
    private TabPane tabPane;

    @FXML
    private ImageView sourceImageView;

    @FXML
    private ImageView targetImageView;

    @FXML
    private CodeEditor codeEditor;

    @FXML
    private Label messageTextArea;

    public ImageProcController() {
        this.imageProcService = new RemoteImageProcService("localhost", 50051);
    }

    @FXML
    private void initialize() {
        this.sourceImageView.imageProperty().bind(this.sourceImage);
        this.targetImageView.imageProperty().bind(this.targetImage);
        this.messageTextArea.visibleProperty().bind(this.message.isNotEmpty());
        this.messageTextArea.textProperty().bind(this.message);
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.getInstance().setRoot("secondary");
    }

    @FXML
    private void loadImage(ActionEvent actionEvent) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        final File file = fileChooser.showOpenDialog(App.getInstance().getStage());

        if (file != null) {
            try {
                this.sourceImage.setValue(loadImage(file));
                this.tabPane.getSelectionModel().select(sourceTab);
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.CLOSE).showAndWait();
            }
        }
    }

    private static byte[] serializeImagePng(Image image) {
        final BufferedImage bimg = SwingFXUtils.fromFXImage(image, null);

        try (final ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            ImageIO.write(bimg, "png", stream);
            return stream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(); // never happens
        }
    }

    private static Image loadImage(File file) throws IOException {
        try (final InputStream is = new FileInputStream(file.getAbsolutePath())) {
            return new Image(is);
        }
    }

    @FXML
    private void processImage(ActionEvent actionEvent) {
        final byte[] imageDataPng = serializeImagePng(this.sourceImage.get());
        final String sourceCode = this.codeEditor.getText();
        this.imageProcService.processImage(sourceCode, imageDataPng).thenAccept(result -> {
            final byte[] resultImageDataPng = result.getImageDataPng();

            Platform.runLater(() -> {
                if (resultImageDataPng.length > 0) {
                    try (final InputStream is = new ByteArrayInputStream(resultImageDataPng)) {
                        this.targetImage.setValue(new Image(is));
                    } catch (Exception e) {
                        new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.CLOSE).showAndWait();
                    }
                }
                this.message.setValue(result.getMessage());
                this.tabPane.getSelectionModel().select(targetTab);
            });
        });
    }

    @FXML
    private void takeImage(ActionEvent actionEvent) {
        this.sourceImage.setValue(this.targetImage.getValue());
    }

    @FXML
    public void saveImageAs(ActionEvent actionEvent) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PNG File", "*.png"));
        final File file = fileChooser.showSaveDialog(App.getInstance().getStage());

        if (file != null) {
            final BufferedImage bimg = SwingFXUtils.fromFXImage(this.targetImage.get(), null);
            try {
                ImageIO.write(bimg, "png", file);
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.CLOSE).showAndWait();
            }
        }
    }

    @FXML
    private void onKeyPressed(KeyEvent keyEvent) {
        if (KEY_COMBINATION_RUN.match(keyEvent)) {
            processImage(new ActionEvent());
        } else if (KEY_COMBINATION_TAKE.match(keyEvent)) {
            takeImage(new ActionEvent());
        } else if (KEY_COMBINATION_SAVEAS.match(keyEvent)) {
            saveImageAs(new ActionEvent());
        }
    }
}
