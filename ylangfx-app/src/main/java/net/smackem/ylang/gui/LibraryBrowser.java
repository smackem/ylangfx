package net.smackem.ylang.gui;

import javafx.event.ActionEvent;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.smackem.ylang.lang.Declaration;
import net.smackem.ylang.model.DeclModel;
import net.smackem.ylang.model.DeclType;
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
        final Button browseButton = new Button("Browse Files...");
        browseButton.setOnAction(ignored -> this.scriptLibrary.browse());
        setTop(new ToolBar(browseButton));
        final TreeView<DeclModel<?>> treeView = new TreeView<>();
        treeView.setCellFactory(this::createCell);
        treeView.setShowRoot(false);
        treeView.setRoot(populateTree());
        setCenter(treeView);
    }

    private TreeCell<DeclModel<? extends Declaration>> createCell(TreeView<DeclModel<? extends Declaration>> treeView) {
        final TreeCell<DeclModel<? extends Declaration>> cell = new TreeCell<>() {
            @Override
            protected void updateItem(DeclModel<?> decl, boolean empty) {
                super.updateItem(decl, empty);
                if (decl == null || empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    setGraphic(getDeclNode(decl));
                }
            }
        };
        cell.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (event.getClickCount() >= 2) {
                final TreeItem<DeclModel<? extends Declaration>> parentItem = cell.getTreeItem().getParent();
                final ModuleDeclModel module = parentItem != null && parentItem.getValue().type() == DeclType.FILE
                        ? (ModuleDeclModel) parentItem.getValue()
                        : null;
                fireEvent(new ItemActionEvent(this, cell.getItem(), module));
            }
        });
        return cell;
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

    public static class ItemActionEvent extends ActionEvent {
        private final DeclModel<?> decl;
        private final ModuleDeclModel module;

        ItemActionEvent(Object source, DeclModel<? extends Declaration> decl, ModuleDeclModel module) {
            super(source, null);
            this.decl = decl;
            this.module = module;
        }

        public DeclModel<? extends Declaration> decl() {
            return this.decl;
        }

        public ModuleDeclModel module() {
            return this.module;
        }
    }
}
