package net.smackem.ylang.gui;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.fxmisc.wellbehaved.event.EventPattern;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;
import org.reactfx.Subscription;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeEditor extends CodeArea {
    private static final Pattern PATTERN_WHITESPACE = Pattern.compile("^\\s+");
    private static final String[] KEYWORDS = new String[] {
            "and", "or", "not", "for", "in", "yield",
            "if", "else", "true", "false", "log",
            "fn", "return", "nil", "while",
    };

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String COLOR_PATTERN = "#[0-9a-fA-F]+(:[0-9a-fA-F]{2})?\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String AT_PATTERN = "@";
    private static final String FNCALL_PATTERN = "(\\b(?!fn)(?:[A-Za-z0-9_]+))\\(";
    private static final String NUMBER_PATTERN = "\\b\\d+(\\.\\d*)?\\b";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<COLOR>" + COLOR_PATTERN + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<AT>" + AT_PATTERN + ")"
            + "|(?<FNCALL>" + FNCALL_PATTERN + ")"
            + "|(?<NUMBER>" + NUMBER_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );

    public CodeEditor() {
        // show line numbers
        setParagraphGraphicFactory(LineNumberFactory.get(this));

        // replace tab with four spaces
        final InputMap<KeyEvent> im = InputMap.consume(
                EventPattern.keyPressed(KeyCode.TAB),
                e -> replaceSelection("    ")
        );
        Nodes.addInputMap(this, im);

        // recompute the syntax highlighting 500 ms after user stops editing area
        final Subscription cleanupWhenNoLongerNeedIt = this
                // plain changes = ignore style changes that are emitted when syntax highlighting is reapplied
                // multi plain changes = save computation by not rerunning the code multiple times
                //   when making multiple changes (e.g. renaming a method at multiple parts in file)
                .multiPlainChanges()
                // do not emit an event until 500 ms have passed since the last emission of previous stream
                .successionEnds(Duration.ofMillis(500))
                // run the following code block when previous stream emits an event
                .subscribe(ignore -> setStyleSpans(0, computeHighlighting(getText())));

        addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
        getStylesheets().add(getClass().getResource("codeeditor.css").toExternalForm());
    }

    private void onKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            int caretPosition = getCaretPosition();
            int currentParagraph = getCurrentParagraph();
            final Matcher matcher = PATTERN_WHITESPACE.matcher(
                    getParagraph(currentParagraph - 1).getSegments().get(0));
            if (matcher.find()) {
                Platform.runLater(() -> insertText(caretPosition, matcher.group()));
            }
        }
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        final Matcher matcher = PATTERN.matcher(text);
        final StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        int lastKwEnd = 0;

        while(matcher.find()) {
            final String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("COLOR") != null ? "color" :
                    matcher.group("PAREN") != null ? "paren" :
                    matcher.group("AT") != null ? "at" :
                    matcher.group("FNCALL") != null ? "fncall" :
                    matcher.group("NUMBER") != null ? "number" :
                    matcher.group("STRING") != null ? "string" :
                    matcher.group("COMMENT") != null ? "comment" :
                    null; // never happens
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }

        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}
