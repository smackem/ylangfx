package net.smackem.ylang.lang;

import org.antlr.v4.runtime.ParserRuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

class BaseVisitor<T> extends YLangBaseVisitor<T> {
    private static final Logger log = LoggerFactory.getLogger(BaseVisitor.class);
    private final List<String> semanticErrors = new ArrayList<>();

    public List<String> semanticErrors() {
        return Collections.unmodifiableList(this.semanticErrors);
    }

    void logSemanticError(ParserRuleContext ctx, String message) {
        final String text = String.format("line %d, char %d: %s",
                ctx.start.getLine(), ctx.start.getCharPositionInLine(), message);
        log.info(text);
        this.semanticErrors.add(text);
    }

    boolean logSemanticErrors(Collection<String> semanticErrors) {
        return this.semanticErrors.addAll(semanticErrors);
    }
}
