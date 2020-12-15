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

    public StringProperty fileName() {
        return this.fileName;
    }

    public StringProperty code() {
        return this.code;
    }
}
