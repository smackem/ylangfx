package net.smackem.ylang.execution;

import net.smackem.ylang.execution.functions.Func;
import net.smackem.ylang.execution.functions.FunctionRegistry;
import net.smackem.ylang.execution.operators.BinaryOperator;
import net.smackem.ylang.execution.operators.UnaryOperator;
import net.smackem.ylang.lang.Instruction;
import net.smackem.ylang.lang.Program;
import net.smackem.ylang.runtime.*;
import net.smackem.ylang.util.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Interpreter {
    private static final Logger log = LoggerFactory.getLogger(Interpreter.class);
    private final Context ctx;
    private final Program program;
    private final TaggedInstruction[] instructions;
    private final Deque<Integer> stackFrames = new ArrayDeque<>();
    private int pc;
    private int stackFrameOffset;

    public Interpreter(Program program, ImageVal inputImage, Writer logWriter) {
        this.program = program;
        this.instructions = program.instructions().stream()
                .map(TaggedInstruction::new)
                .toArray(TaggedInstruction[]::new);
        this.ctx = new Context(inputImage, new Writer() {
            @SuppressWarnings("NullableProblems")
            @Override
            public void write(char[] buf, int off, int len) throws IOException {
                final String str = new String(buf, off, len);
                logWriter.write(str + "\n");
                log.info(str);
            }

            @SuppressWarnings("NullableProblems")
            @Override
            public void write(String str) throws IOException {
                logWriter.write(str + "\n");
                log.info(str);
            }

            @Override
            public void flush() throws IOException {
                logWriter.flush();
            }

            @Override
            public void close() throws IOException {
                logWriter.close();
            }
        });
    }

    public Program program() {
        return this.program;
    }

    Context context() {
        return this.ctx;
    }

    public Value execute() throws ExecutionException {
        final int programSize = this.instructions.length;
        final LocalDateTime begin = LocalDateTime.now();
        RuntimeContext.reset();
        writeLogMessageGuarded("""
                >> execution started @ %s
                   input image: %s""".formatted(
                begin,
                this.ctx.inputImage()));
        while (this.pc < programSize) {
            final TaggedInstruction taggedInstr = this.instructions[pc];
            if (log.isDebugEnabled()) {
                log.debug("@{}: {}, stack.size={}",
                        String.format("%4d", pc),
                        taggedInstr.instruction.opCode(),
                        this.ctx.stack().size());
            }
            try {
                executeInstr(taggedInstr);
            } catch (Exception e) {
                final String message = "execution error @pc=%d instruction=%s debugInfo=%s".formatted(
                        this.pc, taggedInstr.instruction, taggedInstr.instruction.debugInfo());
                final Collection<Instruction> stackTrace = collectStackTrace(taggedInstr.instruction);
                log.error(message, e);
                throw new ExecutionException(message, stackTrace, e);
            }
            this.pc++;
        }
        final Value retVal;
        try {
            retVal = this.ctx.stack().pop();
        } catch (StackException e) {
            throw new ExecutionException("error popping return value:", null, e);
        }
        final LocalDateTime end = LocalDateTime.now();
        final Duration duration = Duration.between(begin, end);
        writeLogMessageGuarded("""
               >> execution finished @ %s
                  elapsed time: %d.%s seconds
                  return value: %s""".formatted(
                end,
                duration.toSeconds(), duration.toMillisPart(),
                Values.prettyPrint(retVal)));
        return retVal;
    }

    private Collection<Instruction> collectStackTrace(Instruction currentInstr) {
        final List<Instruction> instructions = new ArrayList<>();
        instructions.add(currentInstr);
        final LinkedList<Integer> stackFrameOffsets = new LinkedList<>(this.stackFrames);
        while (stackFrameOffsets.isEmpty() == false) {
            final Integer stackFrameOffset = stackFrameOffsets.pop();
            if (stackFrameOffset == 0) {
                break;
            }
            final Value val = this.ctx.stack().get(stackFrameOffset - 1);
            final int pc = val instanceof NumberVal ?  (int) ((NumberVal) val).value() - 1 : 0; // navigate to CALL instruction
            instructions.add(this.instructions[pc].instruction);
        }
        return instructions;
    }

    private void writeLogMessageGuarded(String message) throws ExecutionException {
        try {
            this.ctx.logWriter().write(message);
        } catch (IOException e) {
            log.error("error writing log message", e);
            throw new ExecutionException("error writing log message", null, e);
        }
    }

    private void executeInstr(TaggedInstruction taggedInstr) throws StackException, MissingOverloadException, IOException, PanicException {
        final Stack stack = this.ctx.stack();
        final Instruction instr = taggedInstr.instruction;
        switch (instr.opCode()) {
            case LD_VAL -> stack.push(instr.valueArg());
            case LD_GLB -> stack.push(stack.get(instr.intArg()));
            case LD_ENV -> stack.push(Objects.equals(instr.strArg(), "in") ? this.ctx.inputImage() : NilVal.INSTANCE);
            case ST_GLB -> stack.set(instr.intArg(), stack.pop());
            case LD_LOC -> stack.push(stack.get(instr.intArg() + this.stackFrameOffset));
            case ST_LOC -> stack.set(instr.intArg() + this.stackFrameOffset, stack.pop());
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
                final NumberVal cmp = (NumberVal) BinaryOperator.CMP.invoke(stack.pop(), r);
                stack.push(BoolVal.of(cmp.value() >= 0));
            }
            case GT -> {
                final Value r = stack.pop();
                final NumberVal cmp = (NumberVal) BinaryOperator.CMP.invoke(stack.pop(), r);
                stack.push(BoolVal.of(cmp.value() > 0));
            }
            case LE -> {
                final Value r = stack.pop();
                final NumberVal cmp = (NumberVal) BinaryOperator.CMP.invoke(stack.pop(), r);
                stack.push(BoolVal.of(cmp.value() <= 0));
            }
            case LT -> {
                final Value r = stack.pop();
                final NumberVal cmp = (NumberVal) BinaryOperator.CMP.invoke(stack.pop(), r);
                stack.push(BoolVal.of(cmp.value() < 0));
            }
            case OR -> {
                final BoolVal r = (BoolVal) UnaryOperator.BOOL.invoke(stack.pop());
                final BoolVal l = (BoolVal) UnaryOperator.BOOL.invoke(stack.pop());
                stack.push(BoolVal.of(l.value() || r.value()));
            }
            case AND -> {
                final BoolVal r = (BoolVal) UnaryOperator.BOOL.invoke(stack.pop());
                final BoolVal l = (BoolVal) UnaryOperator.BOOL.invoke(stack.pop());
                stack.push(BoolVal.of(l.value() && r.value()));
            }
            case ADD -> {
                final Value r = stack.pop();
                stack.push(BinaryOperator.ADD.invoke(stack.pop(), r));
            }
            case DIV -> {
                final Value r = stack.pop();
                stack.push(BinaryOperator.DIV.invoke(stack.pop(), r));
            }
            case MOD -> {
                final Value r = stack.pop();
                stack.push(BinaryOperator.MOD.invoke(stack.pop(), r));
            }
            case MUL -> {
                final Value r = stack.pop();
                stack.push(BinaryOperator.MUL.invoke(stack.pop(), r));
            }
            case SUB -> {
                final Value r = stack.pop();
                stack.push(BinaryOperator.SUB.invoke(stack.pop(), r));
            }
            case IDX -> {
                final Value r = stack.pop();
                stack.push(BinaryOperator.INDEX.invoke(stack.pop(), r));
            }
            case IN -> {
                final Value r = stack.pop();
                stack.push(BinaryOperator.IN.invoke(stack.pop(), r));
            }
            case CMP -> {
                final Value r = stack.pop();
                stack.push(BinaryOperator.CMP.invoke(stack.pop(), r));
            }
            case NOT -> stack.push(UnaryOperator.NOT.invoke(stack.pop()));
            case NEG -> stack.push(UnaryOperator.NEG.invoke(stack.pop()));
            case BR_ZERO -> {
                if (((BoolVal) UnaryOperator.NOT.invoke(stack.pop())).value()) {
                    this.pc = instr.intArg() - 1;
                }
            }
            case BR -> this.pc = instr.intArg() - 1;
            case DUP -> {
                final Value v = stack.pop();
                stack.push(v);
                stack.push(v);
            }
            case POP -> stack.pop();
            case INVOKE -> {
                final LinkedList<Value> args = new LinkedList<>();
                for (int i = 0; i < instr.intArg(); i++) {
                    args.addFirst(stack.pop());
                }
                if (taggedInstr.invokedFunc == null || taggedInstr.invokedFuncMatches(args) == false) {
                    taggedInstr.invokedFunc = FunctionRegistry.INSTANCE.getFunc(instr.strArg(), args);
                    taggedInstr.invokedFuncArgs = args;
                }
                final Value result = taggedInstr.invokedFunc.invoke(args);
                stack.push(result);
            }
            case ITER -> {
                final Value iterable = stack.pop();
                if (iterable instanceof Iterable == false) {
                    throw new MissingOverloadException(iterable + " of type " + iterable.type() + " is not iterable!");
                }
                //noinspection unchecked
                stack.push(new IteratorVal(((Iterable<Value>)iterable).iterator()));
            }
            case BR_NEXT -> {
                final IteratorVal iterator = (IteratorVal) stack.pop();
                final Value next = iterator.next();
                if (next != null) {
                    stack.push(next);
                } else {
                    this.pc = instr.intArg() - 1;
                }
            }
            case LABEL-> {
                // nop
            }
            case MK_LIST -> {
                final LinkedList<Value> args = new LinkedList<>();
                for (int i = 0; i < instr.intArg(); i++) {
                    args.addFirst(stack.pop());
                }
                stack.push(new ListVal(args));
            }
            case MK_POINT -> {
                final Value y = stack.pop();
                final Value x = stack.pop();
                stack.push(new PointVal(((NumberVal) x).value(), ((NumberVal) y).value()));
            }
            case MK_RANGE -> {
                final Value upper = stack.pop();
                final Value step = stack.pop();
                final Value lower = stack.pop();
                stack.push(new RangeVal(
                        ((NumberVal) lower).value(),
                        ((NumberVal) upper).value(),
                        ((NumberVal) step).value()));
            }
            case LOG -> {
                final String str = popValuesAsString(stack, instr.intArg());
                this.ctx.logWriter().write(str);
            }
            case CONCAT -> {
                final Value r = stack.pop();
                stack.push(BinaryOperator.CONCAT.invoke(stack.pop(), r));
            }
            case MK_ENTRY -> {
                final Value value = stack.pop();
                final Value key = stack.pop();
                stack.push(new MapEntryVal(key, value));
            }
            case MK_MAP -> {
                final LinkedList<MapEntryVal> entries = new LinkedList<>();
                for (int i = 0; i < instr.intArg(); i++) {
                    entries.addFirst((MapEntryVal) stack.pop());
                }
                stack.push(new MapVal(entries));
            }
            case CALL -> branchToFunction(instr.intArg() - 1, (int) ((NumberVal) instr.valueArg()).value());
            case RET -> {
                final Value retVal = stack.pop(); // return value
                stack.setTail(this.stackFrameOffset);
                this.stackFrameOffset = this.stackFrames.pop();
                final int retPC = (int) ((NumberVal) stack.pop()).value();
                stack.push(retVal);
                this.pc = retPC - 1;
            }
            case LD_FUNC -> {
                final FunctionVal function = new FunctionVal(instr.strArg(),
                        (int) ((NumberVal) instr.valueArg()).value(),
                        args -> executeFunction(instr.intArg(), args));
                stack.push(function);
            }
            case CALL_FUNC -> {
                final LinkedList<Value> args = new LinkedList<>();
                for (int i = 0; i < instr.intArg(); i++) {
                    args.addFirst(stack.pop());
                }
                final FunctionVal function = (FunctionVal) stack.pop();
                final Value retVal = function.invoke(args);
                stack.push(retVal);
            }
            case SET_OPT -> handleOption(instr.strArg(), instr.valueArg());
            case PANIC -> {
                final String str = popValuesAsString(stack, instr.intArg());
                throw new PanicException(str);
            }
            default -> throw new IllegalStateException("Unexpected value: " + instr.opCode());
        }
    }

    private void handleOption(String option, Value value) throws MissingOverloadException {
        if (Objects.equals(option, "DISABLE_YLN")) {
            log.info("setting option {} = {}", option, value);
            final boolean v = ((BoolVal) UnaryOperator.BOOL.invoke(value)).value();
            RuntimeContext.current().disableYln(v);
            return;
        }
        throw new IllegalArgumentException("unknown option '" + option + "'");
    }

    private void branchToFunction(int pc, int argCount) {
        final Stack stack = this.ctx.stack();
        this.stackFrames.push(this.stackFrameOffset);
        stack.insert(stack.size() - argCount, new NumberVal(this.pc + 1));
        this.stackFrameOffset = stack.size() - argCount;
        this.pc = pc;
    }

    private Value executeFunction(int pc, List<Value> args) {
        final int originalStackFrameOffset = this.stackFrameOffset;
        final Stack stack = this.ctx.stack();
        try {
            for (final Value value : args) {
                stack.push(value);
            }
            branchToFunction(pc, args.size());
            while (this.stackFrameOffset != originalStackFrameOffset) {
                executeInstr(this.instructions[this.pc]);
                this.pc++;
            }
            this.pc--;
            return stack.pop();
        } catch (IOException | StackException | MissingOverloadException | PanicException e) {
            log.error("error executing function", e);
            throw new RuntimeException(e);
        }
    }

    private static String popValuesAsString(Stack stack, int count) throws StackException {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            final Value v = stack.pop();
            String s = Values.prettyPrint(v);
            if (v instanceof StringVal) {
                s = s.substring(1, s.length() - 1);
            }
            sb.insert(0, s);
        }
        return sb.toString();
    }

    private static class TaggedInstruction {
        final Instruction instruction;
        Func invokedFunc;
        List<Value> invokedFuncArgs;

        boolean invokedFuncMatches(List<Value> args) {
            int n = args.size();
            for (int i = 0; i < n; i++) {
                if (args.get(i).type() != invokedFuncArgs.get(i).type()) {
                    return false;
                }
            }
            return true;
        }

        TaggedInstruction(Instruction instruction) {
            this.instruction = instruction;
        }
    }
}
