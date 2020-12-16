package net.smackem.ylang.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ScriptModel {
    private final StringProperty fileName = new SimpleStringProperty();
    private final StringProperty code = new SimpleStringProperty();
    private final BooleanProperty dirty = new SimpleBooleanProperty(false);

    public ScriptModel(String fileName, String code) {
        this.fileName.set(fileName);
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
}
