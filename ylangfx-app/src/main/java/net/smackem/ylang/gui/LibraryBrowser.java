package net.smackem.ylang.gui;

import com.google.common.base.Strings;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.geometry.Pos;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class LibraryBrowser extends BorderPane {
    private static final Logger log = LoggerFactory.getLogger(LibraryBrowser.class);
    private final ScriptLibrary scriptLibrary;
    private final Collection<ModuleDeclModel> modules;

    public LibraryBrowser(ScriptLibrary scriptLibrary) {
        this.scriptLibrary = Objects.requireNonNull(scriptLibrary);
        final String cssPath = getClass().getResource("librarybrowser.css").toExternalForm();
        this.getStylesheets().add(cssPath);
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
        final Label matchesLabel = new Label();
        final Button clearSearchButton = new Button("X");
        final ToolBar matchesBox = new ToolBar(matchesLabel, clearSearchButton);
        matchesBox.setVisible(false);
        final TextField searchField = new TextField();
        searchField.setPromptText("Find...");
        searchField.addEventHandler(ActionEvent.ACTION, ev -> {
            if (searchField.getText().isBlank()) {
                clearSearch();
                matchesBox.setVisible(false);
                return;
            }
            int matchCount = searchScripts(searchField);
            matchesLabel.setText("%d matches".formatted(matchCount));
            matchesBox.setVisible(true);
            ev.consume();
        });
        clearSearchButton.setOnAction(ev -> {
            clearSearch();
            searchField.setText("");
            matchesBox.setVisible(false);
        });
        setTop(new ToolBar(browseButton, searchField, matchesBox));
        final TreeView<DeclModel<?>> treeView = new TreeView<>();
        treeView.setCellFactory(this::createCell);
        treeView.setShowRoot(false);
        treeView.setRoot(populateTree());
        setCenter(treeView);
    }

    private int searchScripts(TextField searchField) {
        final String searchText = searchField.getText().toLowerCase();
        final int[] matchCount = new int[1];
        forEachDeclModel(decl -> {
            if (Strings.isNullOrEmpty(decl.docComment())) {
                return;
            }
            final boolean match = decl.signature().toLowerCase().contains(searchText);
            log.info("{}: match = {}", decl.signature(), match);
            if (match) {
                matchCount[0]++;
            }
            decl.highlightedProperty().set(match);
        });
        return matchCount[0];
    }

    private void clearSearch() {
        forEachDeclModel(decl -> decl.highlightedProperty().set(false));
    }

    private void forEachDeclModel(Consumer<DeclModel<?>> action) {
        final Deque<DeclModel<?>> queue = new LinkedList<>(this.modules);
        while (queue.isEmpty() == false) {
            final DeclModel<?> decl = queue.removeFirst();
            action.accept(decl);
            for (final DeclModel<?> child : decl.children()) {
                queue.addLast(child);
            }
        }
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
            case FILE -> createSignatureLabel(decl);
            case GLOBAL -> getGlobalOrFunctionNode(decl, ":=", "globalIcon");
            case FUNCTION -> getGlobalOrFunctionNode(decl, "fn", "functionIcon");
        };
    }

    private Node getGlobalOrFunctionNode(DeclModel<?> decl, String iconText, String styleClass) {
        final String doc = decl.docComment();
        final Label signatureLabel = createSignatureLabel(decl);
        final Label icon = new Label();
        icon.getStyleClass().add(styleClass);
        icon.setPrefSize(20, 16);
        icon.setText(iconText);
        final HBox box = new HBox(icon, signatureLabel);
        box.setAlignment(Pos.CENTER_LEFT);
        if (doc == null || doc.isEmpty()) {
            return box;
        }
        final Label docLabel = new Label(decl.docComment());
        docLabel.getStyleClass().add("docComment");
        return new VBox(box, docLabel);
    }

    private static Label createSignatureLabel(DeclModel<?> decl) {
        final Label label = new Label(decl.signature());
        label.getStyleClass().add("signature");
        if (decl.highlightedProperty().get()) {
            label.getStyleClass().add("highlighted");
        }
        decl.highlightedProperty().addListener((prop, old, val) -> {
            if (val) {
                label.getStyleClass().add("highlighted");
            } else {
                label.getStyleClass().remove("highlighted");
            }
        });
        return label;
    }

    private TreeItem<DeclModel<?>> populateTree() {
        final TreeItem<DeclModel<?>> root = new TreeItem<>(null);
        for (final ModuleDeclModel module : this.modules) {
            final TreeItem<DeclModel<?>> moduleItem = new TreeItem<>(module);
            root.getChildren().add(moduleItem);
            moduleItem.setExpanded(true);
            for (final DeclModel<?> decl : module.children()) {
                if (decl.docComment() != null && decl.docComment().isBlank() == false) {
                    moduleItem.getChildren().add(new TreeItem<>(decl));
                }
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
