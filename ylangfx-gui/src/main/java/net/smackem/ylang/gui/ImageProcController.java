package net.smackem.ylang.gui;

import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import net.smackem.ylang.execution.Interpreter;
import net.smackem.ylang.execution.functions.FunctionRegistry;
import net.smackem.ylang.lang.Compiler;
import net.smackem.ylang.lang.Program;
import net.smackem.ylang.runtime.ImageVal;
import net.smackem.ylang.runtime.Value;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ImageProcController {

    private final ReadOnlyObjectWrapper<Image> sourceImage = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<Image> targetImage = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyStringWrapper message = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper logOutput = new ReadOnlyStringWrapper();
    private final ReadOnlyBooleanWrapper isRunning = new ReadOnlyBooleanWrapper();
    private static final KeyCombination KEY_COMBINATION_RUN = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN);
    private static final KeyCombination KEY_COMBINATION_TAKE = new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN);
    private static final KeyCombination KEY_COMBINATION_SAVEAS = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);

    @FXML
    private Button runButton;
    @FXML
    private TextArea logTextArea;
    @FXML
    private Tab logTab;
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
    @FXML
    private SplitPane splitPane;
    @FXML
    private ToggleButton splitToggle;

    @FXML
    private void initialize() {
        this.sourceImageView.imageProperty().bind(this.sourceImage);
        this.targetImageView.imageProperty().bind(this.targetImage);
        this.messageTextArea.visibleProperty().bind(this.message.isNotEmpty());
        this.messageTextArea.textProperty().bind(this.message);
        this.logTextArea.textProperty().bind(this.logOutput);
        this.runButton.disableProperty().bind(this.isRunning);
        this.codeEditor.appendText("""
                inp := $in.default(#ffffff@00)
                out := image(inp).clip(inp.bounds)
                K := |1  1  1
                      1  1  1
                      1  1  1|
                for p in inp.bounds {
                    out[p] = -inp.convolve(p, K)
                }
                return out
                """);
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
        processImageYLang2();
    }

    private void processImageYLang2() {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile(this.codeEditor.getText(), FunctionRegistry.INSTANCE, errors);
        if (errors.isEmpty() == false) {
            this.message.setValue(String.join("\n", errors));
        }
        if (program == null) {
            return;
        }
        final StringWriter logWriter = new StringWriter();
        final Interpreter interpreter = new Interpreter(program, convertFromFX(this.sourceImage.get()), logWriter);
        final Value result;
        try {
            result = interpreter.execute();
        } catch (Exception e) {
            this.message.setValue(e.getMessage());
            e.printStackTrace();
            return;
        }
        this.message.setValue(null);
        this.logOutput.setValue(logWriter.toString());
        if (result instanceof ImageVal) {
            this.targetImage.set(convertToFX((ImageVal) result));
        }
    }

    private void processImageRemote() {
        this.isRunning.setValue(true);
        final byte[] imageDataPng = serializeImagePng(this.sourceImage.get());
        final String sourceCode = this.codeEditor.getText();
        App.getInstance().getImageProcService().processImage(sourceCode, imageDataPng).thenAccept(result -> {
            final byte[] resultImageDataPng = result.getImageDataPng();

            if (resultImageDataPng.length > 0) {
                try (final InputStream is = new ByteArrayInputStream(resultImageDataPng)) {
                    this.targetImage.setValue(new Image(is));
                } catch (Exception e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.CLOSE).showAndWait();
                }
            }
            this.message.setValue(result.getMessage());
            this.logOutput.setValue(result.getLogOutput());
            this.tabPane.getSelectionModel().select(targetTab);
            this.isRunning.setValue(false);
        });
    }

    private static ImageVal convertFromFX(Image image) {
        if (image == null) {
            return null;
        }
        final PixelReader pixelReader = image.getPixelReader();
        final int width = (int) image.getWidth();
        final int height = (int) image.getHeight();
        final int[] buffer = new int[width * height];
        pixelReader.getPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), buffer, 0, width);
        return ImageVal.fromArgbPixels(width, height, buffer);
    }

    private static Image convertToFX(ImageVal image) {
        final int width = image.width();
        final int height = image.height();
        final WritableImage wImage = new WritableImage(width, height);
        final PixelWriter pixelWriter = wImage.getPixelWriter();
        final int[] buffer = image.toArgbPixels();
        pixelWriter.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), buffer, 0, width);
        return wImage;
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

    @FXML
    private void toggleHorizontalSplit(ActionEvent actionEvent) {
        this.splitPane.setOrientation(this.splitToggle.isSelected()
                ? Orientation.HORIZONTAL
                : Orientation.VERTICAL);
    }
}
