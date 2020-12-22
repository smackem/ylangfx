package net.smackem.ylang.gui;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import net.smackem.ylang.lang.Compiler;
import net.smackem.ylang.model.DeclModel;
import net.smackem.ylang.model.ModuleDeclModel;
import net.smackem.ylang.model.ScriptLibrary;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class LibraryBrowser extends VBox {
    private final ScriptLibrary scriptLibrary;

    public LibraryBrowser(ScriptLibrary scriptLibrary) {
        this.scriptLibrary = Objects.requireNonNull(scriptLibrary);
        try {
            assemble();
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage());
            e.printStackTrace();
        }
    }

    private void assemble() throws IOException {
        final Button browseButton = new Button("Browse Script Library...");
        browseButton.setOnAction(ignored -> this.scriptLibrary.browse());
        this.setFillWidth(true);
        getChildren().add(new ToolBar(browseButton));
        final TreeView<DeclModel<?>> treeView = new TreeView<>();
        treeView.setCellFactory(this::createCell);
        treeView.setShowRoot(false);
        treeView.setRoot(populateTree());
        getChildren().add(treeView);
    }

    private TreeCell<DeclModel<?>> createCell(TreeView<DeclModel<?>> treeView) {
        return new TreeCell<>() {
            @Override
            protected void updateItem(DeclModel decl, boolean empty) {
                if (decl == null || empty) {
                    setGraphic(null);
                } else {
                    setGraphic(getDeclNode(decl));
                }
            }
        };
    }

    private Node getDeclNode(DeclModel<?> decl) {
        return switch (decl.type()) {
            case FILE -> new Label(decl.signature());
            case GLOBAL, FUNCTION -> new VBox(new Label(decl.signature(), new Label(decl.docComment())));
        };
    }

    private TreeItem<DeclModel<?>> populateTree() throws IOException {
        final TreeItem<DeclModel<?>> root = new TreeItem<>(null);
        final Collection<ModuleDeclModel> modules = parseSources();
        for (final ModuleDeclModel module : modules) {
            final TreeItem<DeclModel<?>> moduleItem = new TreeItem<>(module);
            root.getChildren().add(moduleItem);
            moduleItem.setExpanded(true);
            for (final DeclModel<?> decl : module.children()) {
                moduleItem.getChildren().add(new TreeItem<>(decl));
            }
        }
        return root;
    }

    private Collection<ModuleDeclModel> parseSources() throws IOException {
        return this.scriptLibrary.scriptFiles().stream()
                .map(path -> {
                    try {
                        return ModuleDeclModel.extract(path);
                    } catch (IOException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
