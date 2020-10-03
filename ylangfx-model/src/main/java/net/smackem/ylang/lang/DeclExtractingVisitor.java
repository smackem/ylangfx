package net.smackem.ylang.lang;

import org.antlr.v4.runtime.ParserRuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

class DeclExtractingVisitor extends YLangBaseVisitor<Void> {

    private static final Logger log = LoggerFactory.getLogger(DeclExtractingVisitor.class);
    private final Set<String> globals = new HashSet<>();
    private final List<String> semanticErrors = new ArrayList<>();

    public Set<String> globals() {
        return Collections.unmodifiableSet(this.globals);
    }

    public List<String> semanticErrors() {
        return Collections.unmodifiableList(this.semanticErrors);
    }

    @Override
    public Void visitAssignStmt(YLangParser.AssignStmtContext ctx) {
        if (ctx.Decleq() != null) {
            final String ident = ctx.Ident().getText();
            if (this.globals.add(ident) == false) {
                logSemanticError(ctx, "duplicate global variable name '" + ident + "'");
            }
        }
        return super.visitAssignStmt(ctx);
    }

    @Override
    public Void visitForStmt(YLangParser.ForStmtContext ctx) {
        final String ident = ctx.Ident().getText();
        if (this.globals.add(ident) == false) {
            logSemanticError(ctx, "duplicate global variable name '" + ident + "'");
        } else {
            this.globals.add(getIteratorIdent(ident));
        }
        return super.visitForStmt(ctx);
    }

    static String getIteratorIdent(String itemIdent) {
        return "<iter>" + itemIdent;
    }

    private void logSemanticError(ParserRuleContext ctx, String message) {
        final String text = String.format("line %d, char %d: %s",
                ctx.start.getLine(), ctx.start.getCharPositionInLine(), message);
        log.info(text);
        this.semanticErrors.add(text);
    }
}
