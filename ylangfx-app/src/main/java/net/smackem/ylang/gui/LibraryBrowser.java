package net.smackem.ylang.gui;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import net.smackem.ylang.model.DeclModel;
import net.smackem.ylang.model.ScriptLibrary;

import java.util.Objects;

public class LibraryBrowser extends VBox {
    private final ScriptLibrary scriptLibrary;

    public LibraryBrowser(ScriptLibrary scriptLibrary) {
        this.scriptLibrary = Objects.requireNonNull(scriptLibrary);
        assemble();
    }

    public ScriptLibrary scriptLibrary() {
        return this.scriptLibrary;
    }

    private void assemble() {
        final Button browseButton = new Button("Browse Script Library...");
        browseButton.setOnAction(ignored -> this.scriptLibrary.browse());
        this.setFillWidth(true);
        getChildren().add(new ToolBar(browseButton));
        final TreeView<DeclModel> treeView = new TreeView<>();
        treeView.setCellFactory(this::createCell);
        getChildren().add(treeView);
    }

    private TreeCell<DeclModel> createCell(TreeView<DeclModel> treeView) {
        return new TreeCell<>() {
            @Override
            protected void updateItem(DeclModel decl, boolean empty) {
                if (decl == null || empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new Label(decl.toString()));
                }
            }
        };
    }
}
