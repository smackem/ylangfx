package net.smackem.ylang.execution;

import net.smackem.ylang.execution.operators.BinaryOperator;
import net.smackem.ylang.execution.operators.UnaryOperator;
import net.smackem.ylang.lang.Instruction;
import net.smackem.ylang.runtime.BoolVal;
import net.smackem.ylang.runtime.ImageVal;
import net.smackem.ylang.runtime.NumberVal;
import net.smackem.ylang.runtime.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Interpreter {
    private static final Logger log = LoggerFactory.getLogger(Interpreter.class);
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
        int stackFrameIndex = 0;
        final int codeSize = this.code.size();
        final Stack stack = this.ctx.stack();
        while (pc < codeSize) {
            final Instruction instr = this.code.get(pc);
            log.debug("@{}: {}, stack.size={}", pc, instr.opCode(), stack.size());
            switch (instr.opCode()) {
                case LD_VAL -> stack.push(instr.valueArg());
                case LD_GLB -> stack.push(stack.get(instr.intArg()));
                case ST_GLB -> stack.set(instr.intArg(), stack.pop());
                case LD_LOC -> stack.push(stack.get(instr.intArg() + stackFrameIndex));
                case ST_LOC -> stack.set(instr.intArg() + stackFrameIndex, stack.pop());
                case EQ -> {
                    final Value r = stack.pop();
                    stack.push(BoolVal.of(stack.pop().equals(r)));
                }
                case NEQ -> {
                    final Value r = stack.pop();
                    stack.push(BoolVal.of(stack.pop().equals(r) == false));
                }
                case GE -> {
                    final Value r = stack.pop();
                    final NumberVal cmp = (NumberVal) BinaryOperator.CMP.invoke(this.ctx, stack.pop(), r);
                    stack.push(BoolVal.of(cmp.value() >= 0));
                }
                case GT -> {
                    final Value r = stack.pop();
                    final NumberVal cmp = (NumberVal) BinaryOperator.CMP.invoke(this.ctx, stack.pop(), r);
                    stack.push(BoolVal.of(cmp.value() > 0));
                }
                case LE -> {
                    final Value r = stack.pop();
                    final NumberVal cmp = (NumberVal) BinaryOperator.CMP.invoke(this.ctx, stack.pop(), r);
                    stack.push(BoolVal.of(cmp.value() <= 0));
                }
                case LT -> {
                    final Value r = stack.pop();
                    final NumberVal cmp = (NumberVal) BinaryOperator.CMP.invoke(this.ctx, stack.pop(), r);
                    stack.push(BoolVal.of(cmp.value() < 0));
                }
                case OR -> {
                    final BoolVal r = (BoolVal) UnaryOperator.BOOL.invoke(this.ctx, stack.pop());
                    final BoolVal l = (BoolVal) UnaryOperator.BOOL.invoke(this.ctx, stack.pop());
                    stack.push(BoolVal.of(l.value() || r.value()));
                }
                case AND -> {
                    final BoolVal r = (BoolVal) UnaryOperator.BOOL.invoke(this.ctx, stack.pop());
                    final BoolVal l = (BoolVal) UnaryOperator.BOOL.invoke(this.ctx, stack.pop());
                    stack.push(BoolVal.of(l.value() && r.value()));
                }
                case ADD -> {
                    final Value r = stack.pop();
                    stack.push(BinaryOperator.ADD.invoke(this.ctx, stack.pop(), r));
                }
                case DIV -> {
                    final Value r = stack.pop();
                    stack.push(BinaryOperator.DIV.invoke(this.ctx, stack.pop(), r));
                }
                case MOD -> {
                    final Value r = stack.pop();
                    stack.push(BinaryOperator.MOD.invoke(this.ctx, stack.pop(), r));
                }
                case MUL -> {
                    final Value r = stack.pop();
                    stack.push(BinaryOperator.MUL.invoke(this.ctx, stack.pop(), r));
                }
                case SUB -> {
                    final Value r = stack.pop();
                    stack.push(BinaryOperator.SUB.invoke(this.ctx, stack.pop(), r));
                }
                case IDX -> {
                    final Value r = stack.pop();
                    stack.push(BinaryOperator.INDEX.invoke(this.ctx, stack.pop(), r));
                }
                case IN -> {
                    final Value r = stack.pop();
                    stack.push(BinaryOperator.IN.invoke(this.ctx, stack.pop(), r));
                }
                case CMP -> {
                    final Value r = stack.pop();
                    stack.push(BinaryOperator.CMP.invoke(this.ctx, stack.pop(), r));
                }
                case NOT -> stack.push(UnaryOperator.NOT.invoke(this.ctx, stack.pop()));
                case NEG -> stack.push(UnaryOperator.NEG.invoke(this.ctx, stack.pop()));
                case BR_ZERO -> {
                    if (((BoolVal) UnaryOperator.NOT.invoke(this.ctx, stack.pop())).value()) {
                        pc = instr.intArg() - 1;
                    }
                }
                case BR -> pc = instr.intArg() - 1;
                case DUP -> {
                    final Value v = stack.pop();
                    stack.push(v);
                    stack.push(v);
                }
                case LABEL-> {
                    // nop
                }
                default -> throw new IllegalStateException("Unexpected value: " + instr.opCode());
            }
            pc++;
        }
    }
}
