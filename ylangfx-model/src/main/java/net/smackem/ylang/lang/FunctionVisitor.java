package net.smackem.ylang.lang;

import java.util.ArrayList;
import java.util.List;

public class FunctionVisitor extends BaseVisitor<Void> {

    private final List<FunctionDecl> functions = new ArrayList<>();
    private ProgramStructure programStructure;
    private boolean stmtVisited;

    public ProgramStructure programStructure() {
        return this.programStructure;
    }

    @Override
    public Void visitProgram(YLangParser.ProgramContext ctx) {
        super.visitProgram(ctx);
        final AllocVisitor allocVisitor = new AllocVisitor();
        ctx.accept(allocVisitor);
        final FunctionDecl mainBody = new FunctionDecl(null, 0, allocVisitor.uniqueVariableCount());
        this.programStructure = new ProgramStructure(mainBody, this.functions);
        return null;
    }

    @Override
    public Void visitFunctionDecl(YLangParser.FunctionDeclContext ctx) {
        if (this.stmtVisited) {
            logSemanticError(ctx, "function declaration must prepend all statements");
            return null;
        }
        final AllocVisitor allocVisitor = new AllocVisitor();
        ctx.block().accept(allocVisitor);
        final int parameterCount = ctx.parameters() != null
                ? ctx.parameters().Ident().size()
                : 0;
        final FunctionDecl func = new FunctionDecl(ctx.Ident().getText(),
                parameterCount,
                allocVisitor.uniqueVariableCount());
        return null;
    }

    @Override
    public Void visitStatement(YLangParser.StatementContext ctx) {
        this.stmtVisited = true;
        return null;
    }
}
