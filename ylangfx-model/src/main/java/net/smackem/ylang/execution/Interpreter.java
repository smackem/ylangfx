package net.smackem.ylang.execution;

import net.smackem.ylang.execution.operators.BinaryOperator;
import net.smackem.ylang.execution.operators.UnaryOperator;
import net.smackem.ylang.lang.Instruction;
import net.smackem.ylang.runtime.BoolVal;
import net.smackem.ylang.runtime.ImageVal;
import net.smackem.ylang.runtime.NumberVal;

import java.util.List;

public class Interpreter {
    private final Context ctx;
    private final List<Instruction> code;

    public Interpreter(List<Instruction> code, ImageVal inputImage) {
        this.code = code;
        this.ctx = new Context(inputImage);
    }

    Context context() {
        return this.ctx;
    }

    public void execute() throws StackException, MissingOverloadException {
        int pc = 0;
        final int codeSize = this.code.size();
        final var stack = this.ctx.stack();
        while (pc <= codeSize) {
            final Instruction instr = this.code.get(pc);
            boolean branch = false;
            switch (instr.opCode()) {
                case ADD -> {
                    final var r = stack.pop();
                    stack.push(BinaryOperator.ADD.invoke(this.ctx, stack.pop(), r));
                }
                case EQ -> {
                    final var r = stack.pop();
                    stack.push(BoolVal.of(stack.pop().equals(r)));
                }
                case GE -> {
                    final var r = stack.pop();
                    final var cmp = (NumberVal) BinaryOperator.CMP.invoke(this.ctx, stack.pop(), r);
                    stack.push(BoolVal.of(cmp.value() >= 0));
                }
                case GT -> {
                    final var r = stack.pop();
                    final var cmp = (NumberVal) BinaryOperator.CMP.invoke(this.ctx, stack.pop(), r);
                    stack.push(BoolVal.of(cmp.value() > 0));
                }
                case LE -> {
                    final var r = stack.pop();
                    final var cmp = (NumberVal) BinaryOperator.CMP.invoke(this.ctx, stack.pop(), r);
                    stack.push(BoolVal.of(cmp.value() <= 0));
                }
                case LT -> {
                    final var r = stack.pop();
                    final var cmp = (NumberVal) BinaryOperator.CMP.invoke(this.ctx, stack.pop(), r);
                    stack.push(BoolVal.of(cmp.value() < 0));
                }
                case OR -> {
                    final var r = UnaryOperator.BOOL.invoke(this.ctx, stack.pop());
                    final var l = UnaryOperator.BOOL.invoke(this.ctx, stack.pop());
                    stack.push(BoolVal.of(((BoolVal) l).value() || ((BoolVal) r).value()));
                }
                case AND -> {
                    final var r = UnaryOperator.BOOL.invoke(this.ctx, stack.pop());
                    final var l = UnaryOperator.BOOL.invoke(this.ctx, stack.pop());
                    stack.push(BoolVal.of(((BoolVal) l).value() && ((BoolVal) r).value()));
                }
                case DIV -> {
                    final var r = stack.pop();
                    stack.push(BinaryOperator.DIV.invoke(this.ctx, stack.pop(), r));
                }
                case MOD -> {
                    final var r = stack.pop();
                    stack.push(BinaryOperator.MOD.invoke(this.ctx, stack.pop(), r));
                }
                case MUL -> {
                    final var r = stack.pop();
                    stack.push(BinaryOperator.MUL.invoke(this.ctx, stack.pop(), r));
                }
                case NEQ -> {
                    final var r = stack.pop();
                    stack.push(BoolVal.of(stack.pop().equals(r) == false));
                }
                case NOT -> stack.push(UnaryOperator.NOT.invoke(this.ctx, stack.pop()));
                case NEG -> stack.push(UnaryOperator.NEG.invoke(this.ctx, stack.pop()));
                case SUB -> {
                    final var r = stack.pop();
                    stack.push(BinaryOperator.SUB.invoke(this.ctx, stack.pop(), r));
                }
                case DUP, LABEL -> {
                }
                case LD_GLB -> {
                }
                case LD_LOC -> {
                }
                case LD_VAL -> {
                }
                case ST_GLB -> {
                }
                case ST_LOC -> {
                }
                case BR_ZERO -> {
                }
                case BR -> {
                    pc = instr.intArg();
                    branch = true;
                }
                default -> throw new IllegalStateException("Unexpected value: " + instr.opCode());
            }
            if (branch == false) {
                pc++;
            }
        }
    }
}
