package net.smackem.ylang.lang;

import net.smackem.ylang.runtime.NilVal;
import org.antlr.v4.runtime.ParserRuleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

class EmittingVisitor extends YLangBaseVisitor<Void> {

    private static final Logger log = LoggerFactory.getLogger(EmittingVisitor.class);
    private final List<Instruction> instructions = new ArrayList<>();
    private final List<String> semanticErrors = new ArrayList<>();
    private final Map<String, Integer> globals = new HashMap<>();

    public EmittingVisitor(List<String> globals) {
        int index = 0;
        for (final String ident : globals) {
            this.globals.put(ident, index);
        }
    }

    public List<String> semanticErrors() {
        return Collections.unmodifiableList(this.semanticErrors);
    }

    @Override
    public Void visitProgram(YLangParser.ProgramContext ctx) {
        this.globals.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .forEach(entry -> this.instructions.add(new Instruction(OpCode.LD_VAL, NilVal.INSTANCE)));
        return super.visitProgram(ctx);
    }

    @Override
    public Void visitAssignStmt(YLangParser.AssignStmtContext ctx) {
        super.visitAssignStmt(ctx);
        if (ctx.Beq() != null || ctx.Decleq() != null) {
            final String ident = ctx.Ident().getText();
            final Integer addr = this.globals.get(ident);
            if (addr == null) {
                logSemanticError(ctx, "unknown identifier " + ident);
            } else {
                this.instructions.add(new Instruction(OpCode.LD_GLB, addr));
            }
        }
        return null;
    }

    public Program buildProgram() {
        return new Program(this.instructions);
    }

    private void logSemanticError(ParserRuleContext ctx, String message) {
        final String text = String.format("line %d, char %d: %s",
                ctx.start.getLine(), ctx.start.getCharPositionInLine(), message);
        log.info(text);
        this.semanticErrors.add(text);
    }
}
