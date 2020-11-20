package net.smackem.ylang.lang;

import org.antlr.v4.runtime.ParserRuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

class BaseVisitor<T> extends YLangBaseVisitor<T> {
    private static final Logger log = LoggerFactory.getLogger(BaseVisitor.class);
    private final List<String> semanticErrors = new ArrayList<>();
    private final CodeMap codeMap;

    BaseVisitor(CodeMap codeMap) {
        this.codeMap = Objects.requireNonNull(codeMap);
    }

    CodeMap codeMap() {
        return this.codeMap;
    }

    public List<String> semanticErrors() {
        return Collections.unmodifiableList(this.semanticErrors);
    }

    void logSemanticError(ParserRuleContext ctx, String message) {
        final CodeMap.Location loc = this.codeMap.translate(ctx.start.getLine());
        final String text = String.format("file %s line %d:%d: %s",
                loc.fileName(), loc.lineNumber(), ctx.start.getCharPositionInLine(), message);
        log.info(text);
        this.semanticErrors.add(text);
    }

    boolean logSemanticErrors(Collection<String> semanticErrors) {
        return this.semanticErrors.addAll(semanticErrors);
    }
}
