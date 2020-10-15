package net.smackem.ylang;

import net.smackem.ylang.execution.Interpreter;
import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.execution.StackException;
import net.smackem.ylang.execution.functions.FunctionRegistry;
import net.smackem.ylang.lang.Compiler;
import net.smackem.ylang.lang.Program;
import net.smackem.ylang.runtime.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTest {
    @Test
    public void assignments() throws StackException, MissingOverloadException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile("""
                a := 1
                b := 2
                c := a + b
                return c
                """, FunctionRegistry.INSTANCE, errors);
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
                a := #ffcc88@40
                b := |0 1 -1 2|
                c := 120;240
                return [a, b, c]
                """, FunctionRegistry.INSTANCE, errors);
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
                """, FunctionRegistry.INSTANCE, errors);
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
                """, FunctionRegistry.INSTANCE, errors);
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
                """, FunctionRegistry.INSTANCE, errors);
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
                """, FunctionRegistry.INSTANCE, errors);
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
                """, FunctionRegistry.INSTANCE, errors);
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
                """, FunctionRegistry.INSTANCE, errors);
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
                """, FunctionRegistry.INSTANCE, errors);
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
                """, FunctionRegistry.INSTANCE, errors);
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
                """, FunctionRegistry.INSTANCE, errors);
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
                a := b(#a0b0c0)
                c := #a0b0c0
                d := [c][0].r
                e := #ffffff@80.over(#000000)
                return [a, c.r, c.g(), b(c), d, e]
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(program).isNotNull();
        assertThat(errors).isEmpty();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                new NumberVal(0xc0),
                new NumberVal(0xa0),
                new NumberVal(0xb0),
                new NumberVal(0xc0),
                new NumberVal(0xa0),
                new RgbVal(128, 128, 128, 255)
        )));
    }

    @Test
    public void invocationStmts() throws StackException, MissingOverloadException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile("""
                #a0b0c0.b();
                [#aabbcc][0].r()
                return nil
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(program).isNotNull();
        assertThat(errors).isEmpty();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null).execute();
        assertThat(retVal).isEqualTo(NilVal.INSTANCE);
    }

    @Test
    public void indexAssignment() throws StackException, MissingOverloadException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile("""
                c := [1, 2, 3]
                c[0] = 4; c[1] = #ffffff
                return c
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(program).isNotNull();
        assertThat(errors).isEmpty();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                new NumberVal(4),
                new RgbVal(255, 255, 255, 255),
                new NumberVal(3)
        )));
    }

    @Test
    public void compoundIndexAssignment() throws StackException, MissingOverloadException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile("""
                k := |1 2 3 4|
                p := 0;0
                k[p.x;p.y] = 100
                return k
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(program).isNotNull();
        assertThat(errors).isEmpty();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null).execute();
        assertThat(retVal).isEqualTo(new KernelVal(List.of(
                new NumberVal(100),
                new NumberVal(2),
                new NumberVal(3),
                new NumberVal(4)
        )));
    }

    @Test
    public void invertImage() throws StackException, MissingOverloadException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile("""
                inp := $in
                out := image(inp.bounds())
                for p in inp.bounds {
                    out[p] = -inp[p]
                }
                return out
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final ImageVal inputImage = new ImageVal(64, 64);
        final Value retVal = new Interpreter(program, inputImage).execute();
        assertThat(retVal).isInstanceOf(ImageVal.class);
        final ImageVal outputImage = (ImageVal) retVal;
        assertThat(outputImage.width()).isEqualTo(inputImage.width());
        assertThat(outputImage.height()).isEqualTo(inputImage.height());
        for (int y = 0; y < outputImage.height(); y++) {
            for (int x = 0; x < outputImage.width(); x++) {
                assertThat(outputImage.getPixel(x, y)).isEqualTo(inputImage.getPixel(x, y).invert());
            }
        }
    }

    @Test
    public void points() throws StackException, MissingOverloadException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile("""
                pt := 100;200
                return [pt.x, pt.y]
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                new NumberVal(100),
                new NumberVal(200)
        )));
    }

    @Test
    public void rectangles() throws StackException, MissingOverloadException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile("""
                rc1 := rect(100;200, 100, 50)
                rc2 := rect(0;10, 20;30)
                return [rc1, rc2, rc1.right, rc2.bottom]
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                new RectVal(100, 200, 100, 50),
                new RectVal(0, 10, 20, 20),
                new NumberVal(200),
                new NumberVal(30)
        )));
    }

    @Test
    public void ranges() throws StackException, MissingOverloadException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile("""
                contains1 := 5 in 1 .. 10
                contains2 := 5 in 10 .. -1 .. -100
                n1 := 0
                for i in 0 .. 4 {
                    n1 = n1 + i
                }
                n2 := 0
                for i2 in 4 .. -1 .. 0 {
                    n2 = n2 + i2
                }
                return [contains1, contains2, n1, n2]
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                BoolVal.of(true),
                BoolVal.of(true),
                new NumberVal(6),
                new NumberVal(10)
        )));
    }

    @Test
    public void swap() throws StackException, MissingOverloadException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile("""
                a := 1
                b := 2
                a <=> b
                return [a, b]
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                new NumberVal(2),
                new NumberVal(1)
        )));
    }

    @Test
    public void throwsOnIdentDuplicate() throws StackException, MissingOverloadException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile("""
                a := 1
                a := 2
                return a
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).hasSize(1).allMatch(e -> e.contains("duplicate"));
        assertThat(program).isNull();
    }

    @Test
    public void throwsOnIdentDuplicateNested() throws StackException, MissingOverloadException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile("""
                if true {
                    a := 1
                    a := 2
                }
                return a
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).hasSize(1).allMatch(e -> e.contains("duplicate"));
        assertThat(program).isNull();
    }

    @Test
    public void identOverrides() throws StackException, MissingOverloadException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile("""
                a := 1
                if true {
                    a := 2
                }
                return a
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null).execute();
        assertThat(retVal).isEqualTo(NumberVal.ONE);
    }

    @Test
    public void assignIdentNested() throws StackException, MissingOverloadException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile("""
                a := 1
                if true {
                    a = 0
                }
                return a
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null).execute();
        assertThat(retVal).isEqualTo(NumberVal.ZERO);
    }

    @Test
    public void complexIdentNesting() throws StackException, MissingOverloadException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile("""
                a := 1
                if true {
                    b := 2
                    if false {
                        b := 3
                    }
                    c := 4
                    a = c
                }
                return a
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null).execute();
        assertThat(retVal).isEqualTo(new NumberVal(4));
    }

    @Test
    public void nestedForLoopsWithSameIteratorIdent() throws StackException, MissingOverloadException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile("""
                a := 0
                for i in 0 .. 3 {
                    for i in 0 .. 2 {
                        a = a + 1
                    }
                }
                return a
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null).execute();
        assertThat(retVal).isEqualTo(new NumberVal(6));
    }

    @Test
    public void declarationOrder() throws StackException, MissingOverloadException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile("""
                if true {
                    b := a // a not declared
                    a := 1
                }
                return [a, b] // a not declared, b not declared
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors)
                .hasSize(3)
                .allMatch(err -> err.contains("unknown identifier"));
        assertThat(program).isNull();
    }

    @Test
    public void minMax() throws StackException, MissingOverloadException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile("""
                a := min(100, 120)
                b := max(100, 120)
                list := [1, -10, 404.5, 123]
                c := list.min()
                d := list.max()
                kernel := |-1 -2 0 100|
                e := kernel.min
                f := kernel.max
                return [a, b, c, d, e, f]
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                new NumberVal(100),
                new NumberVal(120),
                new NumberVal(-10),
                new NumberVal(404.5f),
                new NumberVal(-2),
                new NumberVal(100)
        )));
    }
}
