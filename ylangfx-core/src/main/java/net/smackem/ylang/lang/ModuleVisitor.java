package net.smackem.ylang.lang;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleVisitor extends BaseVisitor<ModuleDecl> {

    private final List<FunctionDecl> functions = new ArrayList<>();
    private final List<GlobalDecl> globals = new ArrayList<>();

    public ModuleVisitor(CodeMap codeMap) {
        super(codeMap);
    }

    @Override
    public ModuleDecl visitProgram(YLangParser.ProgramContext ctx) {
        if (checkProgramStructure(ctx) == false) {
            return null;
        }
        super.visitProgram(ctx);
        final AllocVisitor allocVisitor = new AllocVisitor(codeMap());
        final int allocCount = ctx.accept(allocVisitor);
        if (logSemanticErrors(allocVisitor.semanticErrors())) {
            return null;
        }
        final FunctionDecl mainBody = FunctionDecl.main(allocCount);
        return semanticErrors().isEmpty()
                ? new ModuleDecl("module", mainBody, this.functions, this.globals)
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
        return ctx != null &&
               ctx.children != null &&
               ctx.children.size() > 1 &&
               ctx.declStmt() == null;
    }

    @Override
    public ModuleDecl visitTopLevelStmt(YLangParser.TopLevelStmtContext ctx) {
        final var stmt = ctx.statement();
        if (stmt != null && stmt.declStmt() != null) {
            final var decl = stmt.declStmt();
            final String docComment = trimDocComment(decl.DocComment());
            globals.add(new GlobalDecl(decl.Ident().getText(), docComment, stmt.declStmt().getStart().getLine()));
            return null;
        }
        return super.visitTopLevelStmt(ctx);
    }

    @Override
    public ModuleDecl visitFunctionDecl(YLangParser.FunctionDeclContext ctx) {
        final AllocVisitor allocVisitor = new AllocVisitor(codeMap());
        final int allocCount = ctx.block().accept(allocVisitor);
        if (logSemanticErrors(allocVisitor.semanticErrors())) {
            return null;
        }
        final List<String> parameters = ctx.parameters() != null
                ? ctx.parameters().Ident().stream().map(ParseTree::getText).collect(Collectors.toList())
                : Collections.emptyList();
        final FunctionDecl func = FunctionDecl.function(ctx.Ident().getText(),
                trimDocComment(ctx.DocComment()),
                ctx.getStart().getLine(),
                parameters,
                allocCount);
        this.functions.add(func);
        return null;
    }

    private static String trimDocComment(TerminalNode docComment) {
        return docComment != null
                ? docComment.getText().replaceAll("///\\s*", "").strip()
                : "";
    }
}
