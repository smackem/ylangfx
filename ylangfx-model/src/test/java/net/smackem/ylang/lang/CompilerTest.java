package net.smackem.ylang.lang;

import net.smackem.ylang.execution.Interpreter;
import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.execution.StackException;
import net.smackem.ylang.runtime.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CompilerTest {
    @Test
    public void assignments() throws StackException, MissingOverloadException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile("""
                a := 1
                b := 2
                c := a + b
                return c
                """, errors);
        assertThat(program).isNotNull();
        assertThat(errors).isEmpty();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null).execute();
        assertThat(retVal).isEqualTo(new NumberVal(3));
    }

    @Test
    public void compoundAtoms() throws StackException, MissingOverloadException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile("""
                a := #ffcc88:40
                b := |0 1 -1 2|
                c := 120;240
                return [a, b, c]
                """, errors);
        assertThat(program).isNotNull();
        assertThat(errors).isEmpty();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                new RgbVal(0xff, 0xcc, 0x88, 0x40),
                new KernelVal(List.of(NumberVal.ZERO, NumberVal.ONE, NumberVal.MINUS_ONE, new NumberVal(2))),
                new PointVal(120, 240)
        )));
    }

    @Test
    public void ternaryExpr() throws StackException, MissingOverloadException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile("""
                a := 100 < 200 ? 1 : 0;
                b := 100 > 200 ? 1 : 0;
                return [a, b]
                """, errors);
        assertThat(program).isNotNull();
        assertThat(errors).isEmpty();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                NumberVal.ONE,
                NumberVal.ZERO
        )));
    }

    @Test
    public void ifStmt() throws StackException, MissingOverloadException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile("""
                a := nil
                if 100 < 200 {
                    a = 1
                }
                return a
                """, errors);
        assertThat(program).isNotNull();
        assertThat(errors).isEmpty();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null).execute();
        assertThat(retVal).isEqualTo(NumberVal.ONE);
    }

    @Test
    public void ifElseStmt() throws StackException, MissingOverloadException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile("""
                a := nil
                if 100 > 200 {
                    a = 1
                } else {
                    a = 0
                }
                return a
                """, errors);
        assertThat(program).isNotNull();
        assertThat(errors).isEmpty();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null).execute();
        assertThat(retVal).isEqualTo(NumberVal.ZERO);
    }

    @Test
    public void ifElseIfStmt() throws StackException, MissingOverloadException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile("""
                a := nil
                if 1 == 2 {
                    a = 2
                } else if 1 == 3 {
                    a = 3
                } else if 1 == 4 {
                    a = 4
                } else if 1 == 1 {
                    a = 1
                } else {
                    a = 0
                }
                return a
                """, errors);
        assertThat(program).isNotNull();
        assertThat(errors).isEmpty();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null).execute();
        assertThat(retVal).isEqualTo(NumberVal.ONE);
    }

    @Test
    public void whileStmt() throws StackException, MissingOverloadException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile("""
                a := 0
                b := 10
                while a < 10 {
                    b = b - 1
                    a = a + 1
                }
                return [a, b]
                """, errors);
        assertThat(program).isNotNull();
        assertThat(errors).isEmpty();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                new NumberVal(10),
                NumberVal.ZERO
        )));
    }

    @Test
    public void prematureReturn() throws StackException, MissingOverloadException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile("""
                while 1 < 10 {
                    return 666;
                }
                return 0;
                """, errors);
        assertThat(program).isNotNull();
        assertThat(errors).isEmpty();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null).execute();
        assertThat(retVal).isEqualTo(new NumberVal(666));
    }

    @Test
    public void forStmt() throws StackException, MissingOverloadException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile("""
                n := 0
                for x in |1 2 3 4| {
                    n = n + x
                }
                return n
                """, errors);
        assertThat(program).isNotNull();
        assertThat(errors).isEmpty();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null).execute();
        assertThat(retVal).isEqualTo(new NumberVal(10));
    }

    @Test
    public void multipleForStmts() throws StackException, MissingOverloadException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile("""
                n := 0
                for x in |1 2 3 4| {
                    n = n + x
                }
                m := 1
                for x1 in [1, 2, 3, 4] {
                    m = m + x1
                }
                return [n, m]
                """, errors);
        assertThat(program).isNotNull();
        assertThat(errors).isEmpty();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                new NumberVal(10),
                new NumberVal(11)
        )));
    }

    @Test
    public void indexedAtom() throws StackException, MissingOverloadException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile("""
                l := [1, 2, 3, 4]
                k := |1 2 3 4|
                return [l[0], k[1;1]]
                """, errors);
        assertThat(program).isNotNull();
        assertThat(errors).isEmpty();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                new NumberVal(1),
                new NumberVal(4)
        )));
    }

    @Test
    public void invocations() throws StackException, MissingOverloadException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile("""
                c := #a0b0c0
                return [c.r, c.g(), b(c)]
                """, errors);
        assertThat(program).isNotNull();
        assertThat(errors).isEmpty();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                new NumberVal(0xa0),
                new NumberVal(0xb0),
                new NumberVal(0xc0)
        )));
    }
}
