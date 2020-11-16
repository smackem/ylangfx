package net.smackem.ylang.execution;

import net.smackem.ylang.lang.Instruction;
import net.smackem.ylang.lang.OpCode;
import net.smackem.ylang.lang.Program;
import net.smackem.ylang.runtime.*;
import org.junit.Test;

import java.io.Writer;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class InterpreterTest {
    @Test
    public void addition() throws ExecutionException {
        // return 1 + 2
        final Program program = new Program(List.of(
                new Instruction(null, OpCode.LD_VAL, new NumberVal(1)),
                new Instruction(null, OpCode.LD_VAL, new NumberVal(2)),
                new Instruction(null, OpCode.ADD)
        ));
        final Interpreter interpreter = new Interpreter(program, null, Writer.nullWriter());
        final Value retVal = interpreter.execute();
        final Stack stack = interpreter.context().stack();
        assertThat(stack.size()).isEqualTo(0);
        assertThat(retVal).isEqualTo(new NumberVal(3));
    }

    @Test
    public void simpleArithmetics() throws ExecutionException {
        // return (1 + 2 - 4) * 123.5
        final Program program = new Program(List.of(
                new Instruction(null, OpCode.LD_VAL, new NumberVal(1)),
                new Instruction(null, OpCode.LD_VAL, new NumberVal(2)),
                new Instruction(null, OpCode.ADD),
                new Instruction(null, OpCode.LD_VAL, new NumberVal(4)),
                new Instruction(null, OpCode.SUB),
                new Instruction(null, OpCode.LD_VAL, new NumberVal(123.5f)),
                new Instruction(null, OpCode.MUL)
        ));
        final Interpreter interpreter = new Interpreter(program, null, Writer.nullWriter());
        final Value retVal = interpreter.execute();
        final Stack stack = interpreter.context().stack();
        assertThat(stack.size()).isEqualTo(0);
        assertThat(retVal).isEqualTo(new NumberVal(-123.5f));
    }

    @Test
    public void loadAndStore() throws ExecutionException {
        // a = 1
        // b = 2
        // return a + b
        final Program program = new Program(List.of(
                new Instruction(null, OpCode.LD_VAL, NilVal.INSTANCE), // a @ 0
                new Instruction(null, OpCode.LD_VAL, NilVal.INSTANCE), // b @ 1
                new Instruction(null, OpCode.LD_VAL, new NumberVal(1)),
                new Instruction(null, OpCode.ST_GLB, 0), // a = 1
                new Instruction(null, OpCode.LD_VAL, new NumberVal(2)),
                new Instruction(null, OpCode.ST_GLB, 1), // b = 2
                new Instruction(null, OpCode.LD_GLB, 0),
                new Instruction(null, OpCode.LD_GLB, 1),
                new Instruction(null, OpCode.ADD) // a + b
        ));
        final Interpreter interpreter = new Interpreter(program, null, Writer.nullWriter());
        final Value retVal = interpreter.execute();
        final Stack stack = interpreter.context().stack();
        assertThat(stack.size()).isEqualTo(2);
        assertThat(stack.get(0)).isEqualTo(new NumberVal(1)); // a
        assertThat(stack.get(1)).isEqualTo(new NumberVal(2)); // b
        assertThat(retVal).isEqualTo(new NumberVal(3)); // a + b
    }

    @Test
    public void relationalOps() throws ExecutionException {
        // return 100 > 50 and 3 = 10 or 1 <= 2
        final Program program = new Program(List.of(
                new Instruction(null, OpCode.LD_VAL, new NumberVal(100)),
                new Instruction(null, OpCode.LD_VAL, new NumberVal(50)),
                new Instruction(null, OpCode.GT),
                new Instruction(null, OpCode.LD_VAL, new NumberVal(3)),
                new Instruction(null, OpCode.LD_VAL, new NumberVal(10)),
                new Instruction(null, OpCode.EQ),
                new Instruction(null, OpCode.AND),
                new Instruction(null, OpCode.LD_VAL, new NumberVal(1)),
                new Instruction(null, OpCode.LD_VAL, new NumberVal(2)),
                new Instruction(null, OpCode.LE),
                new Instruction(null, OpCode.OR)
        ));
        final Interpreter interpreter = new Interpreter(program, null, Writer.nullWriter());
        final Value retVal = interpreter.execute();
        final Stack stack = interpreter.context().stack();
        assertThat(stack.size()).isEqualTo(0);
        assertThat(retVal).isEqualTo(BoolVal.TRUE);
    }

    @Test
    public void simpleConditional() throws ExecutionException {
        //return 100 > 50 ? true : false
        final Program program = new Program(List.of(
                new Instruction(null, OpCode.LD_VAL, new NumberVal(100)),
                new Instruction(null, OpCode.LD_VAL, new NumberVal(50)),
                new Instruction(null, OpCode.EQ),
                new Instruction(null, OpCode.BR_ZERO, 5), // goto 'load true'
                new Instruction(null, OpCode.LD_VAL, BoolVal.FALSE),
                new Instruction(null, OpCode.LD_VAL, BoolVal.TRUE)
        ));
        final Interpreter interpreter = new Interpreter(program, null, Writer.nullWriter());
        final Value retVal = interpreter.execute();
        final Stack stack = interpreter.context().stack();
        assertThat(stack.size()).isEqualTo(0);
        assertThat(retVal).isEqualTo(BoolVal.TRUE);
    }

    @Test
    public void simpleLoop() throws ExecutionException {
        // a = 0
        // i = 10
        // while (i != 0) { a = a + 1; i = i - 1; }
        // return nil
        final Program program = new Program(List.of(
                new Instruction(null, OpCode.LD_VAL, NumberVal.ZERO), // a @ 0
                new Instruction(null, OpCode.LD_VAL, new NumberVal(10)), // i @ 1
                // if i != 0 -> end
                new Instruction(null, OpCode.LABEL, "while"),
                new Instruction(null, OpCode.LD_GLB, 1),
                new Instruction(null, OpCode.LD_VAL, NumberVal.ZERO),
                new Instruction(null, OpCode.NEQ),
                new Instruction(null, OpCode.BR_ZERO, 16),
                // a = a + 1
                new Instruction(null, OpCode.LD_GLB, 0),
                new Instruction(null, OpCode.LD_VAL, NumberVal.ONE),
                new Instruction(null, OpCode.ADD),
                new Instruction(null, OpCode.ST_GLB, 0),
                // i = i - 1
                new Instruction(null, OpCode.LD_GLB, 1),
                new Instruction(null, OpCode.LD_VAL, NumberVal.ONE),
                new Instruction(null, OpCode.SUB),
                new Instruction(null, OpCode.ST_GLB, 1),
                // -> while
                new Instruction(null, OpCode.BR, 2),
                new Instruction(null, OpCode.LABEL, "end"),
                new Instruction(null, OpCode.LD_VAL, NilVal.INSTANCE) // return nil
        ));
        final Interpreter interpreter = new Interpreter(program, null, Writer.nullWriter());
        final Value retVal = interpreter.execute();
        final Stack stack = interpreter.context().stack();
        assertThat(stack.size()).isEqualTo(2);
        assertThat(stack.get(0)).isEqualTo(new NumberVal(10));
    }

    @Test
    public void invoke() throws ExecutionException {
        // a = rgb(255, 128, 64)
        // return a.r
        final Program program = new Program(List.of(
                new Instruction(null, OpCode.LD_VAL, NumberVal.ZERO), // a @ 0
                // a = rgb(255, 128, 64)
                new Instruction(null, OpCode.LD_VAL, new NumberVal(255)),
                new Instruction(null, OpCode.LD_VAL, new NumberVal(128)),
                new Instruction(null, OpCode.LD_VAL, new NumberVal(64)),
                new Instruction(null, OpCode.INVOKE, 3, "rgb"),
                new Instruction(null, OpCode.ST_GLB, 0),
                // a.r()
                new Instruction(null, OpCode.LD_GLB, 0),
                new Instruction(null, OpCode.INVOKE, 1, "r")
        ));
        final Interpreter interpreter = new Interpreter(program, null, Writer.nullWriter());
        final Value retVal = interpreter.execute();
        final Stack stack = interpreter.context().stack();
        assertThat(stack.size()).isEqualTo(1);
        assertThat(retVal).isEqualTo(new NumberVal(255));
    }

    @Test
    public void iterate() throws ExecutionException {
        // pt = 0;0
        // r = rect(0, 0, 2, 2)
        // for p in a { pt = pt + p }
        // return pt
        final Program program = new Program(List.of(
                new Instruction(null, OpCode.LD_VAL, new PointVal(0, 0)), // pt @ 0
                new Instruction(null, OpCode.LD_VAL, new RectVal(0, 0, 2, 2)), // r @ 1
                new Instruction(null, OpCode.LD_VAL, NilVal.INSTANCE), // iterator @ 2
                // iterator = r.iterator()
                new Instruction(null, OpCode.LD_GLB, 1),
                new Instruction(null, OpCode.ITER),
                new Instruction(null, OpCode.ST_GLB, 2),
                // loop:
                new Instruction(null, OpCode.LABEL, "loop"),
                // push iterator.next
                new Instruction(null, OpCode.LD_GLB, 2),
                new Instruction(null, OpCode.BR_NEXT, 13), // goto end if next is nil
                // pt = iterator.next + pt
                new Instruction(null, OpCode.LD_GLB, 0),
                new Instruction(null, OpCode.ADD, 0),
                new Instruction(null, OpCode.ST_GLB, 0),
                new Instruction(null, OpCode.BR, 6), // goto loop
                new Instruction(null, OpCode.LABEL, "end"),
                new Instruction(null, OpCode.LD_GLB, 0)
        ));
        final Interpreter interpreter = new Interpreter(program, null, Writer.nullWriter());
        final Value retVal = interpreter.execute();
        final Stack stack = interpreter.context().stack();
        assertThat(stack.size()).isEqualTo(3);
        assertThat(retVal).isEqualTo(new PointVal(2, 2));
    }

    @Test
    public void list() throws ExecutionException {
        // return [1,2,#112233]
        final Program program = new Program(List.of(
                new Instruction(null, OpCode.LD_VAL, new NumberVal(1)),
                new Instruction(null, OpCode.LD_VAL, new NumberVal(2)),
                new Instruction(null, OpCode.LD_VAL, new RgbVal(10, 20, 30, 255)),
                new Instruction(null, OpCode.MK_LIST, 3)
        ));
        final Interpreter interpreter = new Interpreter(program, null, Writer.nullWriter());
        final Value retVal = interpreter.execute();
        final Stack stack = interpreter.context().stack();
        assertThat(stack.size()).isEqualTo(0);
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                new NumberVal(1),
                new NumberVal(2),
                new RgbVal(10, 20, 30, 255))
        ));
    }

    @Test
    public void point() throws ExecutionException {
        // return [1,2,#112233]
        final Program program = new Program(List.of(
                new Instruction(null, OpCode.LD_VAL, new NumberVal(1)),
                new Instruction(null, OpCode.LD_VAL, new NumberVal(2)),
                new Instruction(null, OpCode.MK_POINT, 3)
        ));
        final Interpreter interpreter = new Interpreter(program, null, Writer.nullWriter());
        final Value retVal = interpreter.execute();
        final Stack stack = interpreter.context().stack();
        assertThat(stack.size()).isEqualTo(0);
        assertThat(retVal).isEqualTo(new PointVal(1, 2));
    }
}
