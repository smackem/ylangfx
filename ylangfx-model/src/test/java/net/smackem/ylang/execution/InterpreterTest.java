package net.smackem.ylang.execution;

import net.smackem.ylang.lang.Instruction;
import net.smackem.ylang.lang.OpCode;
import net.smackem.ylang.runtime.*;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class InterpreterTest {
    @Test
    public void addition() throws StackException, MissingOverloadException {
        // 1 + 2
        final List<Instruction> code = List.of(
                new Instruction(OpCode.LD_VAL, new NumberVal(1)),
                new Instruction(OpCode.LD_VAL, new NumberVal(2)),
                new Instruction(OpCode.ADD)
        );
        final Interpreter interpreter = new Interpreter(code, null);
        interpreter.execute();
        final Stack stack = interpreter.context().stack();
        assertThat(stack.size()).isEqualTo(1);
        assertThat(stack.get(0)).isEqualTo(new NumberVal(3));
    }

    @Test
    public void simpleArithmetics() throws StackException, MissingOverloadException {
        // (1 + 2 - 4) * 123.5
        final List<Instruction> code = List.of(
                new Instruction(OpCode.LD_VAL, new NumberVal(1)),
                new Instruction(OpCode.LD_VAL, new NumberVal(2)),
                new Instruction(OpCode.ADD),
                new Instruction(OpCode.LD_VAL, new NumberVal(4)),
                new Instruction(OpCode.SUB),
                new Instruction(OpCode.LD_VAL, new NumberVal(123.5f)),
                new Instruction(OpCode.MUL)
        );
        final Interpreter interpreter = new Interpreter(code, null);
        interpreter.execute();
        final Stack stack = interpreter.context().stack();
        assertThat(stack.size()).isEqualTo(1);
        assertThat(stack.get(0)).isEqualTo(new NumberVal(-123.5f));
    }

    @Test
    public void loadAndStore() throws StackException, MissingOverloadException {
        // a = 1
        // b = 2
        // a + b
        final List<Instruction> code = List.of(
                new Instruction(OpCode.LD_VAL, NilVal.INSTANCE), // a @ 0
                new Instruction(OpCode.LD_VAL, NilVal.INSTANCE), // b @ 1
                new Instruction(OpCode.LD_VAL, new NumberVal(1)),
                new Instruction(OpCode.ST_GLB, 0), // a = 1
                new Instruction(OpCode.LD_VAL, new NumberVal(2)),
                new Instruction(OpCode.ST_GLB, 1), // b = 2
                new Instruction(OpCode.LD_GLB, 0),
                new Instruction(OpCode.LD_GLB, 1),
                new Instruction(OpCode.ADD) // a + b
        );
        final Interpreter interpreter = new Interpreter(code, null);
        interpreter.execute();
        final Stack stack = interpreter.context().stack();
        assertThat(stack.size()).isEqualTo(3);
        assertThat(stack.get(0)).isEqualTo(new NumberVal(1)); // a
        assertThat(stack.get(1)).isEqualTo(new NumberVal(2)); // b
        assertThat(stack.get(2)).isEqualTo(new NumberVal(3)); // a + b
    }

    @Test
    public void relationalOps() throws StackException, MissingOverloadException {
        // 100 > 50 and 3 = 10 or 1 <= 2
        final List<Instruction> code = List.of(
                new Instruction(OpCode.LD_VAL, new NumberVal(100)),
                new Instruction(OpCode.LD_VAL, new NumberVal(50)),
                new Instruction(OpCode.GT),
                new Instruction(OpCode.LD_VAL, new NumberVal(3)),
                new Instruction(OpCode.LD_VAL, new NumberVal(10)),
                new Instruction(OpCode.EQ),
                new Instruction(OpCode.AND),
                new Instruction(OpCode.LD_VAL, new NumberVal(1)),
                new Instruction(OpCode.LD_VAL, new NumberVal(2)),
                new Instruction(OpCode.LE),
                new Instruction(OpCode.OR)
        );
        final Interpreter interpreter = new Interpreter(code, null);
        interpreter.execute();
        final Stack stack = interpreter.context().stack();
        assertThat(stack.size()).isEqualTo(1);
        assertThat(stack.get(0)).isEqualTo(BoolVal.TRUE);
    }

    @Test
    public void simpleConditional() throws StackException, MissingOverloadException {
        final List<Instruction> code = List.of(
                new Instruction(OpCode.LD_VAL, new NumberVal(100)),
                new Instruction(OpCode.LD_VAL, new NumberVal(50)),
                new Instruction(OpCode.EQ),
                new Instruction(OpCode.BR_ZERO, 5), // goto 'load true'
                new Instruction(OpCode.LD_VAL, BoolVal.FALSE),
                new Instruction(OpCode.LD_VAL, BoolVal.TRUE)
        );
        final Interpreter interpreter = new Interpreter(code, null);
        interpreter.execute();
        final Stack stack = interpreter.context().stack();
        assertThat(stack.size()).isEqualTo(1);
        assertThat(stack.get(0)).isEqualTo(BoolVal.TRUE);
    }

    @Test
    public void simpleLoop() throws StackException, MissingOverloadException {
        // a = 0
        // i = 10
        // while (i != 0) { a = a + 1; i = i - 1; }
        final List<Instruction> code = List.of(
                new Instruction(OpCode.LD_VAL, NumberVal.ZERO), // a @ 0
                new Instruction(OpCode.LD_VAL, new NumberVal(10)), // i @ 1
                // if i != 0 -> end
                new Instruction(OpCode.LABEL, "while"),
                new Instruction(OpCode.LD_GLB, 1),
                new Instruction(OpCode.LD_VAL, NumberVal.ZERO),
                new Instruction(OpCode.NEQ),
                new Instruction(OpCode.BR_ZERO, 16),
                // a = a + 1
                new Instruction(OpCode.LD_GLB, 0),
                new Instruction(OpCode.LD_VAL, NumberVal.ONE),
                new Instruction(OpCode.ADD),
                new Instruction(OpCode.ST_GLB, 0),
                // i = i - 1
                new Instruction(OpCode.LD_GLB, 1),
                new Instruction(OpCode.LD_VAL, NumberVal.ONE),
                new Instruction(OpCode.SUB),
                new Instruction(OpCode.ST_GLB, 1),
                // -> while
                new Instruction(OpCode.BR, 2),
                new Instruction(OpCode.LABEL, "end")
        );
        final Interpreter interpreter = new Interpreter(code, null);
        interpreter.execute();
        final Stack stack = interpreter.context().stack();
        assertThat(stack.size()).isEqualTo(2);
        assertThat(stack.get(0)).isEqualTo(new NumberVal(10));
    }

    @Test
    public void invoke() throws StackException, MissingOverloadException {
        // a = rgb(255, 128, 64)
        // a = a.r
        final List<Instruction> code = List.of(
                new Instruction(OpCode.LD_VAL, NumberVal.ZERO), // a @ 0
                // a = rgb(255, 128, 64)
                new Instruction(OpCode.LD_VAL, new NumberVal(255)),
                new Instruction(OpCode.LD_VAL, new NumberVal(128)),
                new Instruction(OpCode.LD_VAL, new NumberVal(64)),
                new Instruction(OpCode.INVOKE, 3, "rgb"),
                new Instruction(OpCode.ST_GLB, 0),
                // a.r()
                new Instruction(OpCode.LD_GLB, 0),
                new Instruction(OpCode.INVOKE, 1, "r"),
                new Instruction(OpCode.ST_GLB, 0)
        );
        final Interpreter interpreter = new Interpreter(code, null);
        interpreter.execute();
        final Stack stack = interpreter.context().stack();
        assertThat(stack.size()).isEqualTo(1);
        assertThat(stack.get(0)).isEqualTo(new NumberVal(255));
    }

    @Test
    public void iterate() throws StackException, MissingOverloadException {
        // pt = 0;0
        // r = rect(0, 0, 2, 2)
        // for p in a { pt = pt + p }
        final List<Instruction> code = List.of(
                new Instruction(OpCode.LD_VAL, new PointVal(0, 0)), // pt @ 0
                new Instruction(OpCode.LD_VAL, new RectVal(0, 0, 2, 2)), // r @ 1
                new Instruction(OpCode.LD_VAL, NilVal.INSTANCE), // iterator @ 2
                // iterator = r.iterator()
                new Instruction(OpCode.LD_GLB, 1),
                new Instruction(OpCode.ITER),
                new Instruction(OpCode.ST_GLB, 2),
                // loop:
                new Instruction(OpCode.LABEL, "loop"),
                // push iterator.next
                new Instruction(OpCode.LD_GLB, 2),
                new Instruction(OpCode.BR_NEXT, 13), // goto end if next is nil
                // pt = iterator.next + pt
                new Instruction(OpCode.LD_GLB, 0),
                new Instruction(OpCode.ADD, 0),
                new Instruction(OpCode.ST_GLB, 0),
                new Instruction(OpCode.BR, 6), // goto loop
                new Instruction(OpCode.LABEL, "end")
        );
        final Interpreter interpreter = new Interpreter(code, null);
        interpreter.execute();
        final Stack stack = interpreter.context().stack();
        assertThat(stack.size()).isEqualTo(3);
        assertThat(stack.get(0)).isEqualTo(new PointVal(2, 2));
    }
}
