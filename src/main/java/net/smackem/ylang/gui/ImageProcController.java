package net.smackem.ylang.gui;

import java.awt.image.BufferedImage;
import java.io.*;

import javafx.beans.property.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import net.smackem.ylang.model.ProcessImageResult;
import net.smackem.ylang.model.RemoteImageProcService;

import javax.imageio.ImageIO;

public class ImageProcController {

    private final ReadOnlyObjectWrapper<Image> image = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyStringWrapper message = new ReadOnlyStringWrapper();
    private final RemoteImageProcService imageProcService;

    @FXML
    private TextArea sourceCodeTextArea;

    @FXML
    private TextArea messageTextArea;

    @FXML
    private ImageView imageView;

    public ImageProcController() {
        this.imageProcService = new RemoteImageProcService("localhost", 50051);
    }

    @FXML
    private void initialize() {
        this.imageView.imageProperty().bind(this.image);
        this.messageTextArea.visibleProperty().bind(this.message.isNotEmpty());
        this.messageTextArea.textProperty().bind(this.message);
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.getInstance().setRoot("secondary");
    }

    @FXML
    private void sayHello() throws Exception {
        this.imageProcService.sayHello("gurke");
    }

    @FXML
    private void loadImage(ActionEvent actionEvent) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        final File file = fileChooser.showOpenDialog(App.getInstance().getStage());

        if (file != null) {
            try {
                this.image.setValue(loadImage(file));
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.CLOSE).showAndWait();
            }
        }
    }

    private byte[] saveImage() throws Exception {
        final BufferedImage bimg = SwingFXUtils.fromFXImage(this.image.getValue(), null);

        try (final ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            ImageIO.write(bimg, "png", stream);
            return stream.toByteArray();
        }
    }

    private static Image loadImage(File file) throws IOException {
        try (final InputStream is = new FileInputStream(file.getAbsolutePath())) {
            return new Image(is);
        }
    }

    @FXML
    private void processImage(ActionEvent actionEvent) {
        try {
            final byte[] imageDataPng = saveImage();
            final String sourceCode = this.sourceCodeTextArea.getText();
            final ProcessImageResult result = this.imageProcService.processImage(sourceCode, imageDataPng);
            final byte[] resultImageDataPng = result.getImageDataPng();

            if (resultImageDataPng.length > 0) {
                try (final InputStream is = new ByteArrayInputStream(resultImageDataPng)) {
                    this.image.setValue(new Image(is));
                }
            }
            this.message.setValue(result.getMessage());
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.CLOSE).showAndWait();
        }
    }
}
