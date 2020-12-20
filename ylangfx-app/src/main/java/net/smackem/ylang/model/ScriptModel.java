package net.smackem.ylang.model;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;

import java.nio.file.Path;

public class ScriptModel {
    private final StringProperty fileName = new SimpleStringProperty();
    private final StringProperty code = new SimpleStringProperty();
    private final BooleanProperty dirty = new SimpleBooleanProperty(false);
    private final ObjectProperty<Path> path = new SimpleObjectProperty<>();

    public ScriptModel(Path path, String code) {
        this.fileNameProperty().bind(Bindings.createStringBinding(
                () -> {
                    final Path p = this.path.get();
                    return p == null ? null : p.getFileName().toString();
                },
                this.path));
        this.path.set(path);
        this.code.set(code);
    }

    public StringProperty fileNameProperty() {
        return this.fileName;
    }

    public StringProperty codeProperty() {
        return this.code;
    }

    public BooleanProperty dirtyProperty() {
        return this.dirty;
    }

    public ObjectProperty<Path> pathProperty() {
        return this.path;
    }
}
