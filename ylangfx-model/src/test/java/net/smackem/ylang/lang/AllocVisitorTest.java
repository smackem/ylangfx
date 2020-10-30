package net.smackem.ylang.lang;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AllocVisitorTest {

    @Test
    public void stackDepth1() {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final YLangParser.ProgramContext ast = compiler.compileToAst("""
                a := 1
                b := 2
                c := a + b
                return c
                """, errors);
        assertThat(ast).isNotNull();
        assertThat(errors).isEmpty();
        final AllocVisitor visitor = new AllocVisitor();
        final Integer allocCount = ast.accept(visitor);
        assertThat(allocCount).isEqualTo(3);
    }

    @Test
    public void stackDepth2() {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final YLangParser.ProgramContext ast = compiler.compileToAst("""
                a := 1
                if true {
                    b := 2
                    c := 3
                }
                return a
                """, errors);
        assertThat(ast).isNotNull();
        assertThat(errors).isEmpty();
        final AllocVisitor visitor = new AllocVisitor();
        final Integer allocCount = ast.accept(visitor);
        assertThat(allocCount).isEqualTo(3);
    }

    @Test
    public void stackDepth3() {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final YLangParser.ProgramContext ast = compiler.compileToAst("""
                a := 1      // 1
                if true {
                    b := 2  // 2
                    c := 3  // 3 <--
                } else {    // 1
                    b := 2  // 2
                    c := 3  // 3
                }           // 1
                return a
                """, errors);
        assertThat(ast).isNotNull();
        assertThat(errors).isEmpty();
        final AllocVisitor visitor = new AllocVisitor();
        final Integer allocCount = ast.accept(visitor);
        assertThat(allocCount).isEqualTo(3);
    }

    @Test
    public void stackDepth4() {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final YLangParser.ProgramContext ast = compiler.compileToAst("""
                a := 1              // 1
                if true {
                    b := 2          // 2
                    c := 3          // 3
                    if 1 != 1 {
                        d := 4      // 4
                    } else {        // 3
                        d := 5      // 4
                    }
                } else {            // 1
                    b := 2          // 2
                    c := 3          // 3
                    if 2 == 2 {
                        d := 4      // 4
                        if 3 == 3 {
                            e := 5  // 5 <--
                        }           // 4
                    }               // 3
                    if 4 == 4 {
                        f := 6      // 4
                    }
                }                   // 1
                return a
                """, errors);
        assertThat(ast).isNotNull();
        assertThat(errors).isEmpty();
        final AllocVisitor visitor = new AllocVisitor();
        final Integer allocCount = ast.accept(visitor);
        assertThat(allocCount).isEqualTo(5);
    }

    @Test
    public void stackDepth5() {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final YLangParser.ProgramContext ast = compiler.compileToAst("""
                a := 1                  // 1
                for p in 1..2 {         // 2,3
                    b := 2              // 4
                    for p2 in 2..3 {    // 5,6
                        c := 3          // 7
                    }                   // 4
                    d := 4              // 5
                    for p3 in 2..3 {    // 6,7
                        x := 123        // 8    <--
                    }                   // 5
                }                       // 1
                if true {               // 1
                    e := 5              // 2
                }                       // 1
                return a
                """, errors);
        assertThat(ast).isNotNull();
        assertThat(errors).isEmpty();
        final AllocVisitor visitor = new AllocVisitor();
        final Integer allocCount = ast.accept(visitor);
        assertThat(allocCount).isEqualTo(8);
    }

    @Test
    public void errorOnDuplicateIdent1() {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final YLangParser.ProgramContext ast = compiler.compileToAst("""
                a := 1
                a := 2
                return a
                """, errors);
        assertThat(ast).isNotNull();
        final AllocVisitor visitor = new AllocVisitor();
        ast.accept(visitor);
        assertThat(visitor.semanticErrors())
                .hasSize(1)
                .allMatch(err -> err.contains("duplicate"));
    }

    @Test
    public void errorOnDuplicateIdent2() {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final YLangParser.ProgramContext ast = compiler.compileToAst("""
                a := 1
                if true {
                    a := 2 // no error
                }
                a := 3     // error
                return a
                """, errors);
        assertThat(ast).isNotNull();
        final AllocVisitor visitor = new AllocVisitor();
        ast.accept(visitor);
        assertThat(visitor.semanticErrors())
                .hasSize(1)
                .allMatch(err -> err.contains("duplicate"));
    }
}
