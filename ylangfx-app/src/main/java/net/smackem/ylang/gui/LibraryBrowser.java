package net.smackem.ylang.gui;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.smackem.ylang.model.DeclModel;
import net.smackem.ylang.model.ModuleDeclModel;
import net.smackem.ylang.model.ScriptLibrary;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

public class LibraryBrowser extends BorderPane {
    private final ScriptLibrary scriptLibrary;
    private final Collection<ModuleDeclModel> modules;

    public LibraryBrowser(ScriptLibrary scriptLibrary) {
        this.scriptLibrary = Objects.requireNonNull(scriptLibrary);
        Collection<ModuleDeclModel> modules;
        try {
            modules = parseSources();
        } catch (IOException e) {
            modules = Collections.emptyList();
            new Alert(Alert.AlertType.ERROR, e.getMessage());
            e.printStackTrace();
        }
        this.modules = modules;
        assemble();
    }

    private void assemble() {
        final Button browseButton = new Button("Browse Script Library...");
        browseButton.setOnAction(ignored -> this.scriptLibrary.browse());
        setTop(new ToolBar(browseButton));
        final TreeView<DeclModel<?>> treeView = new TreeView<>();
        treeView.setCellFactory(this::createCell);
        treeView.setShowRoot(false);
        treeView.setRoot(populateTree());
        setCenter(treeView);
    }

    private TreeCell<DeclModel<?>> createCell(TreeView<DeclModel<?>> treeView) {
        return new TreeCell<>() {
            @Override
            protected void updateItem(DeclModel decl, boolean empty) {
                super.updateItem(decl, empty);
                if (decl == null || empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(getDeclNode(decl));
                }
            }
        };
    }

    private Node getDeclNode(DeclModel<?> decl) {
        return switch (decl.type()) {
            case FILE -> new Label(decl.signature());
            case GLOBAL -> getGlobalOrFunctionNode(decl, ":=", "globalIcon");
            case FUNCTION -> getGlobalOrFunctionNode(decl, "fn", "functionIcon");
        };
    }

    private Node getGlobalOrFunctionNode(DeclModel<?> decl, String iconText, String styleClass) {
        final String doc = decl.docComment();
        final Label signatureLabel = new Label(decl.signature());
        final Label icon = new Label();
        icon.getStyleClass().add(styleClass);
        icon.setPrefSize(20, 16);
        icon.setText(iconText);
        signatureLabel.getStyleClass().add("signature");
        if (doc != null && doc.isEmpty() == false) {
            final Label docLabel = new Label(decl.docComment());
            docLabel.getStyleClass().add("docComment");
            return new VBox(new HBox(icon, signatureLabel), docLabel);
        }
        return new HBox(icon, signatureLabel);
    }

    private TreeItem<DeclModel<?>> populateTree() {
        final TreeItem<DeclModel<?>> root = new TreeItem<>(null);
        for (final ModuleDeclModel module : this.modules) {
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
                .sorted(Comparator.comparing(ModuleDeclModel::signature))
                .collect(Collectors.toList());
    }
}
