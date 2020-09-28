package net.smackem.ylang.lang;

import org.antlr.v4.runtime.ParserRuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class DeclExtractingVisitor extends YLangBaseVisitor<Void> {

    private static final Logger log = LoggerFactory.getLogger(DeclExtractingVisitor.class);
    private final List<String> globals = new ArrayList<>();
    private final List<String> semanticErrors = new ArrayList<>();

    public List<String> globals() {
        return Collections.unmodifiableList(this.globals);
    }

    public List<String> semanticErrors() {
        return Collections.unmodifiableList(this.semanticErrors);
    }

    @Override
    public Void visitAssignStmt(YLangParser.AssignStmtContext ctx) {
        if (ctx.Decleq() != null) {
            final String ident = ctx.Ident().getText();
            if (this.globals.contains(ident)) {
                logSemanticError(ctx, "duplicate global variable name '" + ident + "'");
            } else {
                this.globals.add(ctx.Ident().getText());
            }
        }
        return super.visitAssignStmt(ctx);
    }

    private void logSemanticError(ParserRuleContext ctx, String message) {
        final String text = String.format("line %d, char %d: %s",
                ctx.start.getLine(), ctx.start.getCharPositionInLine(), message);
        log.info(text);
        this.semanticErrors.add(text);
    }
}
