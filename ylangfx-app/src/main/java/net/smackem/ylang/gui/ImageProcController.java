package net.smackem.ylang.gui;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;
import net.smackem.ylang.execution.ExecutionException;
import net.smackem.ylang.execution.Interpreter;
import net.smackem.ylang.execution.functions.FunctionRegistry;
import net.smackem.ylang.lang.Compiler;
import net.smackem.ylang.lang.Instruction;
import net.smackem.ylang.lang.Program;
import net.smackem.ylang.model.ImageConversion;
import net.smackem.ylang.model.ScriptModel;
import net.smackem.ylang.model.Yli;
import net.smackem.ylang.model.ScriptLibrary;
import net.smackem.ylang.runtime.*;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.model.TwoDimensional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.prefs.Preferences;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ImageProcController {
    private static final String PREF_SOURCE = "imageProc.source";
    private static final String PREF_IMAGE_PATH = "imageProc.imagePath";
    private static final String PREF_HORIZONTAL_SPLIT = "imageProc.horizontalSplit";
    private static final String PREF_DIVIDER_POS = "imageProc.dividerPos";
    private final ReadOnlyObjectWrapper<Image> sourceImage = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyStringWrapper message = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper logOutput = new ReadOnlyStringWrapper();
    private final ReadOnlyBooleanWrapper isRunning = new ReadOnlyBooleanWrapper();
    private final BooleanProperty horizontalSplit = new SimpleBooleanProperty();
    private final ScriptLibrary scriptLibrary;
    private static final KeyCombination KEY_COMBINATION_RUN = new KeyCodeCombination(KeyCode.F5);
    private static final KeyCombination KEY_COMBINATION_SAVE = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
    private final Collection<ScriptModel> scripts = new ArrayList<>();

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
    private Label messageTextArea;
    @FXML
    private SplitPane splitPane;
    @FXML
    private ToggleButton splitToggle;
    @FXML
    private SplitMenuButton openMenuButton;
    @FXML
    private TabPane scriptsTabPane;
    @FXML
    private VBox targetContainer;

    @FXML
    private void initialize() {
        this.sourceImageView.imageProperty().bind(this.sourceImage);
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
        addScript(new ScriptModel(null, source));
        this.horizontalSplit.set(prefs.getBoolean(PREF_HORIZONTAL_SPLIT, false));
        Platform.runLater(() -> {
            this.runButton.getScene().getWindow().addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, this::onWindowCloseRequest);
            final String imagePath = prefs.get(PREF_IMAGE_PATH, null);
            if (imagePath != null) {
                loadImageFromFile(new File(imagePath));
            }
        });
        this.messageTextArea.maxWidthProperty().bind(this.targetTab.getTabPane().widthProperty());
    }

    private void onWindowCloseRequest(Event event) {
        final Preferences prefs = Preferences.userNodeForPackage(ImageProcController.class);
        this.scripts.stream().findFirst().ifPresent(s ->
                prefs.put(PREF_SOURCE, s.codeProperty().get()));
        prefs.putBoolean(PREF_HORIZONTAL_SPLIT, this.horizontalSplit.get());
        prefs.putDouble(PREF_DIVIDER_POS, this.splitPane.getDividerPositions()[0]);
        if (this.scripts.stream().anyMatch(ScriptModel::isDirtyFile)) {
            final ButtonType buttonType = new Alert(Alert.AlertType.CONFIRMATION, "There are unsaved scripts. Exit anyway?", ButtonType.YES, ButtonType.NO)
                    .showAndWait()
                    .orElse(ButtonType.NO);
            if (buttonType == ButtonType.NO) {
                event.consume();
            }
        }
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
        this.targetContainer.getChildren().clear();
        try {
            program = compiler.compile(selectedScript().codeProperty().get(), FunctionRegistry.INSTANCE, this.scriptLibrary, errors);
        } catch (Exception e) {
            e.printStackTrace();
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
        final Interpreter interpreter = new Interpreter(program,
                ImageConversion.convertFromFX(this.sourceImage.get()), logWriter);
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
        addImages(result);
    }

    private void addImages(Value result) {
        if (result instanceof MatrixVal<?>) {
            this.targetContainer.getChildren().add(buildImageNode(ImageConversion.convertToFX((ImageVal) result)));
        } else if (result instanceof ListVal) {
            addImages(StreamSupport.stream(((ListVal) result).spliterator(), false));
        } else if (result instanceof MapVal) {
            addImages(((MapVal) result).entries().values().stream());
        }
    }

    private void addImages(Stream<Value> values) {
        values.filter(value -> value instanceof MatrixVal<?>)
                .map(value -> ImageConversion.convertToFX((MatrixVal<?>) value))
                .map(this::buildImageNode)
                .forEach(this.targetContainer.getChildren()::add);
    }

    private Node buildImageNode(Image image) {
        final ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        final ContextMenu menu = new ContextMenu(
                newMenuItem("Save As...", ignored -> saveImageAs(image)),
                newMenuItem("Take", ignored -> this.sourceImage.setValue(image)));
        imageView.setOnContextMenuRequested(event -> menu.show(imageView, event.getScreenX(), event.getScreenY()));
        return imageView;
    }

    private static MenuItem newMenuItem(String text, EventHandler<ActionEvent> handler) {
        final MenuItem menuItem = new MenuItem(text);
        menuItem.setOnAction(handler);
        return menuItem;
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
        final String sourceCode = selectedScript().codeProperty().get();
        App.getInstance().getImageProcService().processImage(sourceCode, imageDataPng).thenAccept(result -> {
            final byte[] resultImageDataPng = result.getImageDataPng();

            if (resultImageDataPng.length > 0) {
                try (final InputStream is = new ByteArrayInputStream(resultImageDataPng)) {
                    this.targetContainer.getChildren().clear();
                    this.targetContainer.getChildren().add(new ImageView(new Image(is)));
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

    public void saveImageAs(Image image) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PNG File", "*.png"));
        final File file = fileChooser.showSaveDialog(App.getInstance().getStage());

        if (file != null) {
            final BufferedImage bimg = SwingFXUtils.fromFXImage(image, null);
            try {
                ImageIO.write(bimg, "png", file);
                Yli.saveImage(image, Paths.get(file.getAbsolutePath() + Yli.FILE_EXTENSION));
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.CLOSE).showAndWait();
            }
        }
    }

    @FXML
    private void onKeyPressed(KeyEvent keyEvent) {
        if (KEY_COMBINATION_RUN.match(keyEvent)) {
            processImage(new ActionEvent());
        } else if (KEY_COMBINATION_SAVE.match(keyEvent)) {
            saveScript(new ActionEvent());
        }
    }

    @FXML
    private void fillScriptsMenu(Event event) throws IOException {
        final ObservableList<MenuItem> items = this.openMenuButton.getItems();
        items.clear();
        for (final Path path : this.scriptLibrary.scriptFiles()) {
            items.add(newMenuItem(path.getFileName().toString(), ignored -> openScript(path, null)));
        }
    }

    private void openScript(Path path, Integer lineNumber) {
        final ScriptModel script = this.scripts.stream()
                .filter(s -> Objects.equals(s.pathProperty().get(), path))
                .findFirst()
                .orElse(null);
        if (script != null) {
            final Tab tab = this.scriptsTabPane.getTabs().stream()
                    .filter(t -> t.getUserData() == script)
                    .findFirst()
                    .orElseThrow();
            scriptsTabPane.getSelectionModel().select(tab);
            if (lineNumber != null) {
                script.selectedLineProperty().set(lineNumber);
            }
            return;
        }

        final String code;
        try {
            code = Files.readString(path);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.CLOSE).showAndWait();
            return;
        }
        final ScriptModel newScript = new ScriptModel(path, code);
        addScript(newScript);
        if (lineNumber != null) {
            newScript.selectedLineProperty().set(lineNumber);
        }
    }

    private void addScript(ScriptModel script) {
        this.scripts.add(script);
        final CodeEditor editor = new CodeEditor();
        editor.setId("editor");
        editor.replaceText(script.codeProperty().get());
        editor.multiPlainChanges().successionEnds(Duration.ofMillis(100)).subscribe(ignored -> {
            script.codeProperty().set(editor.getText());
            script.dirtyProperty().set(true);
        });
        script.selectedLineProperty().addListener((prop, old, val) -> {
            if (val != null) {
                final int pos = editor.position((int) val - 1, 0).toOffset();
                editor.moveTo(pos);
                editor.requestFollowCaret();
            }
        });
        final Tab tab = new Tab();
        tab.textProperty().bind(Bindings.when(
                script.fileNameProperty().isNull())
                .then("   *   ")
                .otherwise(Bindings.when(script.dirtyProperty())
                        .then(script.fileNameProperty().concat(" *"))
                        .otherwise(script.fileNameProperty())));
        tab.setClosable(script.fileNameProperty().get() != null);
        tab.setContent(new VirtualizedScrollPane<>(editor));
        tab.setUserData(script);
        this.scriptsTabPane.getTabs().add(tab);
        this.scriptsTabPane.getSelectionModel().select(tab);
        tab.setOnCloseRequest(event -> onTabCloseRequest(event, script));
    }

    private void onTabCloseRequest(Event event, ScriptModel script) {
        if (script.dirtyProperty().get()) {
            final ButtonType result = new Alert(Alert.AlertType.CONFIRMATION, "Save Changes?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL)
                    .showAndWait()
                    .orElse(ButtonType.CANCEL);
            if (result == ButtonType.YES) {
                saveScriptAs(script);
            } else if (result == ButtonType.CANCEL) {
                event.consume();
                return;
            }
        }
        this.scripts.remove(script);
    }

    @FXML
    private void openScriptFile(ActionEvent actionEvent) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Script File");
        fileChooser.setInitialDirectory(this.scriptLibrary.basePath().toFile());
        final File file = fileChooser.showOpenDialog(App.getInstance().getStage());

        if (file != null) {
            openScript(file.toPath(), null);
        }
    }

    @FXML
    private void saveScriptAs(ActionEvent actionEvent) {
        saveScriptAs(selectedScript());
    }

    private void saveScriptAs(ScriptModel script) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Script File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("YLang File", "*.ylang"));
        fileChooser.setInitialDirectory(this.scriptLibrary.basePath().toFile());
        final String fileName = script.fileNameProperty().get();
        if (fileName != null && fileName.isEmpty() == false) {
            fileChooser.setInitialFileName(fileName);
        }
        final File file = fileChooser.showSaveDialog(App.getInstance().getStage());

        if (file != null) {
            saveScript(script, file.toPath());
        }
    }

    private ScriptModel selectedScript() {
        final Object selected = this.scriptsTabPane.getSelectionModel().selectedItemProperty().get().getUserData();
        return (ScriptModel) selected;
    }

    private void saveScript(ScriptModel script, Path path) {
        try {
            Files.writeString(path, script.codeProperty().get());
            script.dirtyProperty().set(false);
            script.pathProperty().set(path);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.CLOSE).showAndWait();
        }
    }

    @FXML
    private void saveScript(ActionEvent ignored) {
        final ScriptModel script = selectedScript();
        if (script == null) {
            return;
        }
        final String fileName = script.fileNameProperty().get();
        if (fileName == null || fileName.isEmpty()) {
            saveScriptAs(script);
            return;
        }
        saveScript(script, script.pathProperty().get());
    }

    @FXML
    private void openLibraryBrowser(ActionEvent ignored) {
        final LibraryBrowser libraryBrowser = new LibraryBrowser(this.scriptLibrary);
        libraryBrowser.addEventHandler(ActionEvent.ACTION, ev -> {
            LibraryBrowser.ItemActionEvent iae = (LibraryBrowser.ItemActionEvent) ev;
            if (iae.module() != null) {
                openScript(iae.module().path(), iae.decl().decl().lineNumber());
            }
        });
        final Tab tab = new Tab("Library Browser", libraryBrowser);
        tabPane.getTabs().add(tab);
        tabPane.getSelectionModel().select(tab);
    }
}
