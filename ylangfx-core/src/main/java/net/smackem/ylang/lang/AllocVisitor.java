package net.smackem.ylang.lang;

import org.antlr.v4.runtime.ParserRuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

class AllocVisitor extends BaseVisitor<Integer> {

    private static final Logger log = LoggerFactory.getLogger(AllocVisitor.class);
    private final Deque<DeclScope> scopes = new ArrayDeque<>();
    private int stackDepth;
    private int uniqueVariableCount;

    AllocVisitor(CodeMap codeMap) {
        super(codeMap);
        this.scopes.push(new DeclScope());
    }

    @Override
    protected Integer defaultResult() {
        return this.uniqueVariableCount;
    }

    @Override
    public Integer visitFunctionDecl(YLangParser.FunctionDeclContext ctx) {
        return defaultResult(); // skip all function definitions when walking main body
    }

    @Override
    public Integer visitBlock(YLangParser.BlockContext ctx) {
        enterScope();
        super.visitBlock(ctx);
        leaveScope();
        return defaultResult();
    }

    @Override
    public Integer visitDeclStmt(YLangParser.DeclStmtContext ctx) {
        final String ident = ctx.Ident().getText();
        addVariable(ctx, ident);
        return super.visitDeclStmt(ctx);
    }

    @Override
    public Integer visitForStmt(YLangParser.ForStmtContext ctx) {
        enterScope();
        final String ident = ctx.Ident().getText();
        addVariable(ctx, ident);
        addVariable(ctx, getIteratorIdent(ident));
        super.visitForStmt(ctx);
        leaveScope();
        return defaultResult();
    }

    static String getIteratorIdent(String itemIdent) {
        return "<iter>" + itemIdent;
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
        if (currentScope().variableIdents.add(ident) == false) {
            logSemanticError(ctx, "duplicate variable name '" + ident + "'");
        }
        this.stackDepth++;
        if (this.stackDepth > this.uniqueVariableCount) {
            log.debug("new unique variable count: {}", this.stackDepth);
            this.uniqueVariableCount = this.stackDepth;
        }
    }

    private static class DeclScope {
        final Set<String> variableIdents = new HashSet<>();
    }
}
