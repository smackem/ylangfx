package net.smackem.ylang.lang;

import net.smackem.ylang.execution.ExecutionException;
import net.smackem.ylang.execution.Interpreter;
import net.smackem.ylang.execution.functions.FunctionRegistry;
import net.smackem.ylang.lang.Compiler;
import net.smackem.ylang.lang.Program;
import net.smackem.ylang.runtime.*;
import org.junit.Ignore;
import org.junit.Test;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class IntegrationTest {
    @Test
    public void assignments() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                a := 1
                b := 2
                c := a + b
                return c
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(program).isNotNull();
        assertThat(errors).isEmpty();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new NumberVal(3));
    }

    @Test
    public void compoundAtoms() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                a := #ffcc88@40
                b := |0 1 -1 2|
                c := 120;240
                return [a, b, c]
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(program).isNotNull();
        assertThat(errors).isEmpty();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                new RgbVal(0xff, 0xcc, 0x88, 0x40),
                new KernelVal(List.of(NumberVal.ZERO, NumberVal.ONE, NumberVal.MINUS_ONE, new NumberVal(2))),
                new PointVal(120, 240)
        )));
    }

    @Test
    public void ternaryExpr() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                a := 100 < 200 ? 1 : 0
                b := 100 > 200 ? 1 : 0
                return [a, b]
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(program).isNotNull();
        assertThat(errors).isEmpty();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                NumberVal.ONE,
                NumberVal.ZERO
        )));
    }

    @Test
    public void ifStmt() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                a := nil
                if 100 < 200 {
                    a = 1
                }
                return a
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(program).isNotNull();
        assertThat(errors).isEmpty();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(NumberVal.ONE);
    }

    @Test
    public void ifElseStmt() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
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
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(NumberVal.ZERO);
    }

    @Test
    public void ifElseIfStmt() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
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
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(NumberVal.ONE);
    }

    @Test
    public void whileStmt() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
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
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                new NumberVal(10),
                NumberVal.ZERO
        )));
    }

    @Test
    public void prematureReturn() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                while 1 < 10 {
                    return 666
                }
                return 0
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(program).isNotNull();
        assertThat(errors).isEmpty();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new NumberVal(666));
    }

    @Test
    public void forStmt() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                n := 0
                for x in |1 2 3 4| {
                    n = n + x
                }
                return n
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(program).isNotNull();
        assertThat(errors).isEmpty();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new NumberVal(10));
    }

    @Test
    public void multipleForStmts() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
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
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                new NumberVal(10),
                new NumberVal(11)
        )));
    }

    @Test
    public void forStmtWithWhereClause() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                n := 0
                for x in |1 2 3 4| where x > 2 {
                    n = n + x
                }
                return n
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(program).isNotNull();
        assertThat(errors).isEmpty();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new NumberVal(7));
    }

    @Test
    public void indexedAtom() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                l := [1, 2, 3, 4]
                k := |1 2 3 4|
                return [l[0], k[1;1]]
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(program).isNotNull();
        assertThat(errors).isEmpty();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                new NumberVal(1),
                new NumberVal(4)
        )));
    }

    @Test
    public void invocations() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                a := b(#a0b0c0)
                c := #a0b0c0
                d := [c][0].r
                e := #ffffff@80.over(#000000)
                return [a, c.r, c.g(), b(c), d, e]
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(program).isNotNull();
        assertThat(errors).isEmpty();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
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
    public void invocationStmts() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                #a0b0c0.b()
                [#aabbcc][0].r()
                return nil
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(program).isNotNull();
        assertThat(errors).isEmpty();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(NilVal.INSTANCE);
    }

    @Test
    public void indexAssignment() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                c := [1, 2, 3]
                c[0] = 4
                c[1] = #ffffff
                return c
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(program).isNotNull();
        assertThat(errors).isEmpty();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                new NumberVal(4),
                new RgbVal(255, 255, 255, 255),
                new NumberVal(3)
        )));
    }

    @Test
    public void compoundIndexAssignment() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                k := |1 2 3 4|
                p := 0;0
                k[p.x;p.y] = 100
                return k
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(program).isNotNull();
        assertThat(errors).isEmpty();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new KernelVal(List.of(
                new NumberVal(100),
                new NumberVal(2),
                new NumberVal(3),
                new NumberVal(4)
        )));
    }

    @Test
    public void invertImage() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
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
        final Value retVal = new Interpreter(program, inputImage, Writer.nullWriter()).execute();
        assertThat(retVal).isInstanceOf(ImageVal.class);
        final ImageVal outputImage = (ImageVal) retVal;
        assertThat(outputImage.width()).isEqualTo(inputImage.width());
        assertThat(outputImage.height()).isEqualTo(inputImage.height());
        for (int y = 0; y < outputImage.height(); y++) {
            for (int x = 0; x < outputImage.width(); x++) {
                assertThat(outputImage.get(x, y)).isEqualTo(inputImage.get(x, y).invert());
            }
        }
    }

    @Test
    public void points() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                pt := 100;200
                return [pt.x, pt.y]
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                new NumberVal(100),
                new NumberVal(200)
        )));
    }

    @Test
    public void rectangles() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                rc1 := rect(100;200, 100, 50)
                rc2 := rect(0;10, 20;30)
                return [rc1, rc2, rc1.right, rc2.bottom]
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                new RectVal(100, 200, 100, 50),
                new RectVal(0, 10, 20, 20),
                new NumberVal(200),
                new NumberVal(30)
        )));
    }

    @Test
    public void ranges() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
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
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                BoolVal.of(true),
                BoolVal.of(true),
                new NumberVal(6),
                new NumberVal(10)
        )));
    }

    @Test
    public void swap() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                a := 1
                b := 2
                a <=> b
                return [a, b]
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                new NumberVal(2),
                new NumberVal(1)
        )));
    }

    @Test
    public void throwsOnIdentDuplicate() {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                a := 1
                a := 2
                return a
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).hasSize(1).allMatch(e -> e.contains("duplicate"));
        assertThat(program).isNull();
    }

    @Test
    public void throwsOnIdentDuplicateNested() {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
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
    public void identOverrides() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                a := 1
                if true {
                    a := 2
                }
                return a
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(NumberVal.ONE);
    }

    @Test
    public void assignIdentNested() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                a := 1
                if true {
                    a = 0
                }
                return a
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(NumberVal.ZERO);
    }

    @Test
    public void complexIdentNesting() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
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
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new NumberVal(4));
    }

    @Test
    public void nestedForLoopsWithSameIteratorIdent() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
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
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new NumberVal(6));
    }

    @Test
    public void declarationOrder() {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
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
    public void minMax() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
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
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                new NumberVal(100),
                new NumberVal(120),
                new NumberVal(-10),
                new NumberVal(404.5f),
                new NumberVal(-2),
                new NumberVal(100)
        )));
    }

    @Test
    public void log() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                log("a: ", 1, " b: ", 2, " c: ", 3)
                return nil
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        new Interpreter(program, null, Writer.nullWriter()).execute();
    }

    @Test
    public void lists() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                a := []
                a.push(1).push(2).push(3)
                b := list(a)
                popped := b.pop()
                first := b.removeAt(0)
                size := a.size()
                c := a :: b
                d := list(2)
                e := list(2, 0)
                f := [4, 6, #0a0b0c] 
                return [a, b, popped, first, size, c, d, e, c.sum, f.sum]
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                new ListVal(List.of(new NumberVal(1), new NumberVal(2), new NumberVal(3))),
                new ListVal(List.of(new NumberVal(2))),
                new NumberVal(3),
                new NumberVal(1),
                new NumberVal(3),
                new ListVal(List.of(new NumberVal(1), new NumberVal(2), new NumberVal(3), new NumberVal(2))),
                new ListVal(List.of(NilVal.INSTANCE, NilVal.INSTANCE)),
                new ListVal(List.of(NumberVal.ZERO, NumberVal.ZERO)),
                new NumberVal(8),
                new RgbVal(0xa + 10, 0xb + 10, 0xc + 10, 255)
        )));
    }

    @Test
    public void lists2() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                a := [1,2,3]
                return a.reverse()
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(
                new ListVal(List.of(new NumberVal(3), new NumberVal(2), new NumberVal(1)))
        );
    }

    @Test
    public void kernels() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                a := |1 2 3 4|
                a[0] = 100
                b := |2 4 3 1|.sort()
                return [a, a.size, a.width, a.height, a.sum, b]
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                new KernelVal(List.of(new NumberVal(100), new NumberVal(2), new NumberVal(3), new NumberVal(4))),
                new NumberVal(4),
                new NumberVal(2),
                new NumberVal(2),
                new NumberVal(109),
                new KernelVal(List.of(new NumberVal(1), new NumberVal(2), new NumberVal(3), new NumberVal(4)))
        )));
    }

    @Test
    public void filters() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                log("gauss")
                log(gaussian(1))
                log(gaussian(2))
                log(gaussian(3))
                log(gaussian(5))
                log(gaussian(10))
                log("laplace")
                log(laplace(1))
                log(laplace(2))
                log(laplace(3))
                return 0
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        new Interpreter(program, null, Writer.nullWriter()).execute();
    }

    @Test
    public void maps() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                a := {
                    loc: 1;2,
                    col: #ff0000,
                    num: 100,
                }
                a.loc = 2;3
                a["col"] = #00ff00
                b := {}
                b.xyz = 123
                b[#ff0000] = 234
                return [a, a.num, a["num"], a["nope"], b, b.size]
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                new MapVal(List.of(
                    new MapEntryVal(new StringVal("loc"), new PointVal(2, 3)),
                    new MapEntryVal(new StringVal("col"), new RgbVal(0, 0xff, 0, 0xff)),
                    new MapEntryVal(new StringVal("num"), new NumberVal(100))
                )),
                new NumberVal(100),
                new NumberVal(100),
                NilVal.INSTANCE,
                new MapVal(List.of(
                    new MapEntryVal(new StringVal("xyz"), new NumberVal(123)),
                    new MapEntryVal(new RgbVal(0xff, 0, 0, 0xff), new NumberVal(234))
                )),
                new NumberVal(2)
        )));
    }

    @Test
    public void chainedInvocations() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                inp := image(rect(0;0, 10, 10)).clip(rect(0;0, 10, 10)).default(#ffffff)
                return 0
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        new Interpreter(program, null, Writer.nullWriter()).execute();
    }

    @Test
    public void ignoredFunctions() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                fn ignored1(x) {
                    return x
                }
                a := 100
                fn ignored2() {
                    loc := a
                    return loc
                }
                b := 200
                return [a, b]
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                new NumberVal(100),
                new NumberVal(200)
        )));
    }

    @Test
    public void callFunctionWithOneParameter() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                fn timesTwo(x) {
                    return x * 2
                }
                return timesTwo(3)
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new NumberVal(6));
    }

    @Test
    public void callFunctionWithoutParameters() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                fn ret100() {
                    return 100
                }
                return ret100()
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new NumberVal(100));
    }

    @Test
    public void callFunctionWithMultipleParameters() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                fn add(a, b) {
                    return a + b
                }
                return add(1, 2)
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new NumberVal(3));
    }

    @Test
    public void callFunctionsRecursively() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                count := 0
                fn recurse(x) {
                    if x > 0 {
                        recurse(x - 1)
                    }
                    count = count + 1
                }
                recurse(3)
                return count
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new NumberVal(4));
    }

    @Test
    public void callFunctionsInReverseDeclOrder() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                count := 0
                fn ping(x) {
                    return pong(x + 1)
                }
                fn pong(x) {
                    return x + 1
                }
                return ping(1)
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new NumberVal(3));
    }

    @Test
    public void functionReferencesWithoutArguments() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                count := 0
                fn doIt() {
                    count = 1
                    return count
                }
                fun := @doIt
                return fun@()
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new NumberVal(1));
    }

    @Test
    public void functionReferencesWithArguments() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                count := 0
                fn doIt(x, y) {
                    count = y - x
                    return count
                }
                fun := @doIt
                return fun@(100, 500)
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new NumberVal(400));
    }

    @Test
    public void functionReferencesWithWrongArguments() {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                count := 0
                fn doIt(x, y) {
                    count = y - x
                    return count
                }
                fun := @doIt
                return fun@(100)
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final Interpreter interpreter = new Interpreter(program, null, Writer.nullWriter());
        assertThatThrownBy(interpreter::execute).isInstanceOf(ExecutionException.class);
    }

    @Test
    public void functionReferencesRepeated() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                fn doIt(x) {
                    return x*2
                }
                fun := @doIt
                list := []
                for i in 0 .. 10 {
                    list.push(fun@(i))
                }
                return list
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final Interpreter interpreter = new Interpreter(program, null, Writer.nullWriter());
        final Value retVal = interpreter.execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                new NumberVal(0),
                new NumberVal(2),
                new NumberVal(4),
                new NumberVal(6),
                new NumberVal(8),
                new NumberVal(10),
                new NumberVal(12),
                new NumberVal(14),
                new NumberVal(16),
                new NumberVal(18)
        )));
    }

    @Test
    public void sortList() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                list := [4, 3, 100, -1]
                return list.sort()
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                new NumberVal(-1),
                new NumberVal(3),
                new NumberVal(4),
                new NumberVal(100)
        )));
    }

    @Test
    public void sortListWithCompareCallback() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                list := [{x: 100}, {x: -1}, {x: 23}]
                fn compare(a, b) {
                    return a["x"] ~ b["x"]
                }
                return list.sort(@compare)
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        final Value retVal = new Interpreter(program, null, Writer.nullWriter()).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                new MapVal(List.of(new MapEntryVal(new StringVal("x"), new NumberVal(-1)))),
                new MapVal(List.of(new MapEntryVal(new StringVal("x"), new NumberVal(23)))),
                new MapVal(List.of(new MapEntryVal(new StringVal("x"), new NumberVal(100))))
        )));
    }

    @Test
    //@Ignore
    public void benchmarkConvolveImage() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                K := kernel(11, 11, 1)
                inp := image(300, 300)
                out := image(inp)
                for i in 0 .. 10 {
                    for p in inp.bounds {
                        out[p] = inp.convolve(p, K)
                    }
                }
                return nil
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        new Interpreter(program, null, Writer.nullWriter()).execute();
    }

    @Test
    //@Ignore
    public void benchmarkConvolveImage2() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                K := kernel(11, 11, 1)
                inp := image(300, 300)
                for i in 0 .. 10 {
                    out := inp.convolve(K)
                }
                return nil
                """, FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        new Interpreter(program, null, Writer.nullWriter()).execute();
    }
}
