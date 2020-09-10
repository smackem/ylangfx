package net.smackem.ylang.lang;

public class EmittingVisitor extends YLangBaseVisitor<Void> {
    @Override
    public Void visitProgram(YLangParser.ProgramContext ctx) {
        return super.visitProgram(ctx);
    }
}
