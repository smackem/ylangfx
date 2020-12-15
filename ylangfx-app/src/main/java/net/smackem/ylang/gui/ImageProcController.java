package net.smackem.ylang.gui;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;
import net.smackem.ylang.execution.ExecutionException;
import net.smackem.ylang.execution.Interpreter;
import net.smackem.ylang.execution.functions.FunctionRegistry;
import net.smackem.ylang.lang.Compiler;
import net.smackem.ylang.lang.Instruction;
import net.smackem.ylang.lang.Program;
import net.smackem.ylang.model.ScriptModel;
import net.smackem.ylang.model.Yli;
import net.smackem.ylang.model.ScriptLibrary;
import net.smackem.ylang.runtime.ImageVal;
import net.smackem.ylang.runtime.Value;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.prefs.Preferences;

public class ImageProcController {

    private static final String PREF_SOURCE = "imageProc.source";
    private static final String PREF_IMAGE_PATH = "imageProc.imagePath";
    private static final String PREF_HORIZONTAL_SPLIT = "imageProc.horizontalSplit";
    private static final String PREF_DIVIDER_POS = "imageProc.dividerPos";
    private final ReadOnlyObjectWrapper<Image> sourceImage = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<Image> targetImage = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyStringWrapper message = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper logOutput = new ReadOnlyStringWrapper();
    private final ReadOnlyBooleanWrapper isRunning = new ReadOnlyBooleanWrapper();
    private final BooleanProperty horizontalSplit = new SimpleBooleanProperty();
    private final ScriptLibrary scriptLibrary;
    private static final KeyCombination KEY_COMBINATION_RUN = new KeyCodeCombination(KeyCode.F5);
    private static final KeyCombination KEY_COMBINATION_TAKE = new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN);
    private static final KeyCombination KEY_COMBINATION_SAVE_AS = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
    private final Collection<ScriptModel> scripts = new ArrayList<ScriptModel>();

    public ImageProcController() {
        ScriptLibrary lib = null;
        try {
            lib = ScriptLibrary.fromDirectory(System.getProperty("user.home"));
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR,
                    "Error opening script library under user home directory: " + e.getMessage()).show();
        }
        if (lib == null) {
            try {
                // fallback to current directory. if this also fails, all hope is lost.
                lib = ScriptLibrary.fromDirectory(System.getProperty("user.dir"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        this.scriptLibrary = lib;
    }

    @FXML
    private Button runButton;
    @FXML
    private TextArea logTextArea;
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
    private SplitMenuButton openMenuButton;
    @FXML
    private TabPane codeTabPane;

    @FXML
    private void initialize() {
        this.sourceImageView.imageProperty().bind(this.sourceImage);
        this.targetImageView.imageProperty().bind(this.targetImage);
        this.messageTextArea.visibleProperty().bind(this.message.isNotEmpty());
        this.messageTextArea.textProperty().bind(this.message);
        this.logTextArea.textProperty().bind(this.logOutput);
        this.runButton.disableProperty().bind(this.isRunning);
        this.splitToggle.selectedProperty().bindBidirectional(this.horizontalSplit);
        this.splitPane.orientationProperty().bind(Bindings.when(this.horizontalSplit)
                .then(Orientation.HORIZONTAL)
                .otherwise(Orientation.VERTICAL));
        final Preferences prefs = Preferences.userNodeForPackage(ImageProcController.class);
        final String source = prefs.get(PREF_SOURCE, """
                fn invert_and_blur(inp) {
                    out := image(inp).clip(inp.bounds)
                    K := |1  1  1
                          1  1  1
                          1  1  1|
                    for p in inp.bounds {
                        out[p] = -inp.convolve(p, K)
                    }
                    return out
                }
                inp := $in.default(#ffffff@00)
                return invert_and_blur(inp)
                """);

        this.splitPane.setDividerPositions(prefs.getDouble(PREF_DIVIDER_POS, 0.6));
        this.codeEditor.replaceText(source);
        this.horizontalSplit.set(prefs.getBoolean(PREF_HORIZONTAL_SPLIT, false));
        Platform.runLater(() -> {
            this.runButton.getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, ignored -> {
                prefs.put(PREF_SOURCE, this.codeEditor.getText());
                prefs.putBoolean(PREF_HORIZONTAL_SPLIT, this.horizontalSplit.get());
                prefs.putDouble(PREF_DIVIDER_POS, this.splitPane.getDividerPositions()[0]);
            });
            final String imagePath = prefs.get(PREF_IMAGE_PATH, null);
            if (imagePath != null) {
                loadImageFromFile(new File(imagePath));
            }
        });
        this.messageTextArea.maxWidthProperty().bind(this.targetTab.getTabPane().widthProperty());
        this.codeTabPane.addEventHandler(Tab.TAB_CLOSE_REQUEST_EVENT, this::onCodeTabClosing);
    }

    @FXML
    private void loadImage(ActionEvent actionEvent) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        final File file = fileChooser.showOpenDialog(App.getInstance().getStage());

        if (file != null) {
            loadImageFromFile(file);
            final Preferences prefs = Preferences.userNodeForPackage(ImageProcController.class);
            prefs.put(PREF_IMAGE_PATH, file.getAbsolutePath());
        }
    }

    private void loadImageFromFile(File file) {
        try {
            this.sourceImage.setValue(loadImage(file));
            this.tabPane.getSelectionModel().select(sourceTab);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.CLOSE).showAndWait();
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
            if (file.getName().endsWith(Yli.FILE_EXTENSION)) {
                return Yli.loadImage(file.toPath());
            }
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
        final Program program;
        try {
            program = compiler.compile(this.codeEditor.getText(), FunctionRegistry.INSTANCE, this.scriptLibrary, errors);
        } catch (Exception e) {
            this.message.setValue(e.getMessage());
            return;
        }
        if (errors.isEmpty() == false) {
            this.message.setValue(String.join(System.lineSeparator(), errors));
        }
        if (program == null) {
            return;
        }
        final StringWriter logWriter = new StringWriter();
        final Interpreter interpreter = new Interpreter(program, convertFromFX(this.sourceImage.get()), logWriter);
        final Value result;
        try {
            result = interpreter.execute();
        } catch (ExecutionException e) {
            final String message = buildErrorMessage(e);
            this.message.setValue(message);
            logWriter.append(message);
            this.logOutput.setValue(logWriter.toString());
            e.printStackTrace();
            return;
        }
        this.message.setValue(null);
        this.logOutput.setValue(logWriter.toString());
        if (result instanceof ImageVal) {
            this.targetImage.set(convertToFX((ImageVal) result));
        }
    }

    private String buildErrorMessage(ExecutionException e) {
        final StringBuilder sb = new StringBuilder();
        sb.append(e.getMessage()).append(System.lineSeparator())
                .append("caused by ").append(e.getCause().getClass()).append(": ").append(System.lineSeparator())
                .append("    ").append(e.getCause().getMessage());
        if (e.stackTrace() != null) {
            for (final Instruction instr : e.stackTrace()) {
                sb.append(System.lineSeparator()).append("    ").append(instr.debugInfo()).append(" ").append(instr);
            }
        }
        return sb.toString();
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
                Yli.saveImage(this.targetImage.get(), Paths.get(file.getAbsolutePath() + Yli.FILE_EXTENSION));
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
        } else if (KEY_COMBINATION_SAVE_AS.match(keyEvent)) {
            saveImageAs(new ActionEvent());
        }
    }

    @FXML
    private void fillScriptsMenu(Event event) throws IOException {
        final ObservableList<MenuItem> items = this.openMenuButton.getItems();
        items.clear();
        for (final Path path : this.scriptLibrary.scriptFiles()) {
            final MenuItem menuItem = new MenuItem(path.getFileName().toString());
            menuItem.setOnAction(ignored -> openScript(path));
            items.add(menuItem);
        }
    }

    private void openScript(Path path) {
        try {
            this.codeEditor.replaceText(Files.readString(path));
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.CLOSE).showAndWait();
        }
    }

    @FXML
    private void openScriptFile(ActionEvent actionEvent) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Script File");
        fileChooser.setInitialDirectory(this.scriptLibrary.basePath().toFile());
        final File file = fileChooser.showOpenDialog(App.getInstance().getStage());

        if (file != null) {
            openScript(file.toPath());
        }
    }

    @FXML
    private void browseLibrary(ActionEvent actionEvent) {
        this.scriptLibrary.browse();
    }

    @FXML
    private void saveScriptAs(ActionEvent actionEvent) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Script File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("YLang File", "*.ylang"));
        fileChooser.setInitialDirectory(this.scriptLibrary.basePath().toFile());
        final File file = fileChooser.showSaveDialog(App.getInstance().getStage());

        if (file != null) {
            try {
                Files.writeString(file.toPath(), this.codeEditor.getText());
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.CLOSE).showAndWait();
            }
        }
    }

    private <T extends Event> void onCodeTabClosing(T e) {
    }
}
