package net.smackem.ylang.lang;

import java.util.ArrayList;
import java.util.List;

public class ModuleVisitor extends BaseVisitor<ModuleDecl> {

    private final List<FunctionDecl> functions = new ArrayList<>();

    @Override
    public ModuleDecl visitProgram(YLangParser.ProgramContext ctx) {
        if (checkProgramStructure(ctx) == false) {
            return null;
        }
        super.visitProgram(ctx);
        final AllocVisitor allocVisitor = new AllocVisitor();
        ctx.accept(allocVisitor);
        final FunctionDecl mainBody = new FunctionDecl(null, 0, allocVisitor.uniqueVariableCount());
        return semanticErrors().isEmpty()
                ? new ModuleDecl(mainBody, this.functions)
                : null;
    }

    private boolean checkProgramStructure(YLangParser.ProgramContext ctx) {
        boolean inBody = false;
        for (final var tls : ctx.topLevelStmt()) {
            if (tls.functionDecl() != null) {
                if (inBody) {
                    logSemanticError(tls, "function declaration must prepend all statements");
                    return false;
                }
            } else if (isBodyStatement(tls.statement())) {
                inBody = true;
            }
        }
        return true;
    }

    private static boolean isBodyStatement(YLangParser.StatementContext ctx) {
        // if statement only contains one child, it is LineBreak
        // declStmts are allowed both in body and in declaration
        return ctx.children.size() > 1 && ctx.declStmt() == null;
    }

    @Override
    public ModuleDecl visitFunctionDecl(YLangParser.FunctionDeclContext ctx) {
        final AllocVisitor allocVisitor = new AllocVisitor();
        ctx.block().accept(allocVisitor);
        final int parameterCount = ctx.parameters() != null
                ? ctx.parameters().Ident().size()
                : 0;
        final FunctionDecl func = new FunctionDecl(ctx.Ident().getText(),
                parameterCount,
                allocVisitor.uniqueVariableCount());
        this.functions.add(func);
        return null;
    }
}
