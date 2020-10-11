package net.smackem.ylang.lang;

import org.antlr.v4.runtime.ParserRuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

class DeclExtractingVisitor extends YLangBaseVisitor<Void> {

    private static final Logger log = LoggerFactory.getLogger(DeclExtractingVisitor.class);
    private final Set<String> globals = new HashSet<>();
    private final List<String> semanticErrors = new ArrayList<>();
    private final Deque<DeclScope> scopes = new ArrayDeque<>();
    int stackDepth;
    int maxStackDepth;

    DeclExtractingVisitor() {
        this.scopes.push(new DeclScope());
    }

    public Set<String> globals() {
        return Collections.unmodifiableSet(this.globals);
    }

    public List<String> semanticErrors() {
        return Collections.unmodifiableList(this.semanticErrors);
    }

    @Override
    public Void visitBlock(YLangParser.BlockContext ctx) {
        enterScope();
        super.visitBlock(ctx);
        leaveScope();
        return null;
    }

    @Override
    public Void visitAssignStmt(YLangParser.AssignStmtContext ctx) {
        if (ctx.Decleq() != null) {
            final String ident = ctx.Ident().getText();
            addVariable(ctx, ident);
        }
        return super.visitAssignStmt(ctx);
    }

    @Override
    public Void visitForStmt(YLangParser.ForStmtContext ctx) {
        enterScope();
        final String ident = ctx.Ident().getText();
        addVariable(ctx, ident);
        addVariable(ctx, getIteratorIdent(ident));
        super.visitForStmt(ctx);
        leaveScope();
        return null;
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

    private DeclScope currentScope() {
        return this.scopes.peek();
    }

    private void enterScope() {
        this.scopes.push(new DeclScope());
    }

    private void leaveScope() {
        final DeclScope scope = this.scopes.pop();
        this.stackDepth -= scope.variableIdents.size();
    }

    private void addVariable(ParserRuleContext ctx, String ident) {
        this.globals.add(ident);
        if (currentScope().variableIdents.add(ident) == false) {
            logSemanticError(ctx, "duplicate variable name '" + ident + "'");
        }
        this.stackDepth++;
        if (this.stackDepth > this.maxStackDepth) {
            this.maxStackDepth = this.stackDepth;
        }
    }

    private static class DeclScope {
        final Set<String> variableIdents = new HashSet<>();
    }
}
