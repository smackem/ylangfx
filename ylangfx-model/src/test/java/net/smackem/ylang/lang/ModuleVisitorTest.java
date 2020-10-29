package net.smackem.ylang.lang;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ModuleVisitorTest {
    @Test
    public void minimalProgram() {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final YLangParser.ProgramContext ast = compiler.compileToAst("""
                return nil
                """, errors);
        assertThat(ast).isNotNull();
        assertThat(errors).isEmpty();
        final ModuleVisitor visitor = new ModuleVisitor();
        final ModuleDecl module = ast.accept(visitor);
        assertThat(module).isNotNull();
        assertThat(visitor.semanticErrors()).isEmpty();
        assertThat(module.functions()).isEmpty();
        assertThat(module.mainBody()).isNotNull();
        assertThat(module.mainBody().localCount()).isEqualTo(0);
    }

    @Test
    public void singleMainBody() {
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
        final ModuleVisitor visitor = new ModuleVisitor();
        final ModuleDecl module = ast.accept(visitor);
        assertThat(module).isNotNull();
        assertThat(visitor.semanticErrors()).isEmpty();
        assertThat(module.functions()).isEmpty();
        assertThat(module.mainBody()).isNotNull();
        assertThat(module.mainBody().localCount()).isEqualTo(3);
    }

    @Test
    public void simpleFunction() {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final YLangParser.ProgramContext ast = compiler.compileToAst("""
                fn doIt() {
                }
                return nil
                """, errors);
        assertThat(ast).isNotNull();
        assertThat(errors).isEmpty();
        final ModuleVisitor visitor = new ModuleVisitor();
        final ModuleDecl module = ast.accept(visitor);
        assertThat(module).isNotNull();
        assertThat(visitor.semanticErrors()).isEmpty();
        assertThat(module.mainBody()).isNotNull();
        assertThat(module.mainBody().localCount()).isEqualTo(0);
        assertThat(module.functions()).hasSize(1);
        assertThat(module.functions().keySet()).containsExactly("doIt");
        final FunctionDecl function = module.functions().values().iterator().next();
        assertThat(function.localCount()).isEqualTo(0);
        assertThat(function.name()).isEqualTo("doIt");
        assertThat(function.parameterCount()).isEqualTo(0);
    }

    @Test
    public void complexFunction() {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final YLangParser.ProgramContext ast = compiler.compileToAst("""
                fn add(a, b) {
                    result := a + b
                    return result
                }
                return nil
                """, errors);
        assertThat(ast).isNotNull();
        assertThat(errors).isEmpty();
        final ModuleVisitor visitor = new ModuleVisitor();
        final ModuleDecl module = ast.accept(visitor);
        assertThat(module).isNotNull();
        assertThat(visitor.semanticErrors()).isEmpty();
        assertThat(module.mainBody()).isNotNull();
        assertThat(module.mainBody().localCount()).isEqualTo(0);
        assertThat(module.functions()).hasSize(1);
        assertThat(module.functions().keySet()).containsExactly("add");
        final FunctionDecl function = module.functions().values().iterator().next();
        assertThat(function.localCount()).isEqualTo(1);
        assertThat(function.name()).isEqualTo("add");
        assertThat(function.parameterCount()).isEqualTo(2);
    }

    @Test
    public void multipleFunctionsAndMainBody() {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final YLangParser.ProgramContext ast = compiler.compileToAst("""
                fn withOneParam(a) {
                    return a
                }
                fn withTwoParamsAndThreeLocals(a, b) {
                    x := a
                    y := b
                    z := a - b
                    return [x, y, z]
                }
                fn withThreeParamsAndOneLocal(a, b, c) {
                    result := a * b * c
                    return result
                }
                top := 0
                for p in $in {
                    top = top + 1
                }
                return nil
                """, errors);
        assertThat(ast).isNotNull();
        assertThat(errors).isEmpty();
        final ModuleVisitor visitor = new ModuleVisitor();
        final ModuleDecl module = ast.accept(visitor);
        assertThat(module).isNotNull();
        assertThat(visitor.semanticErrors()).isEmpty();
        assertThat(module.mainBody()).isNotNull();
        assertThat(module.mainBody().localCount()).isEqualTo(3);
        assertThat(module.functions()).hasSize(3);
        assertThat(module.functions().keySet()).containsOnly("withOneParam", "withTwoParamsAndThreeLocals", "withThreeParamsAndOneLocal");
        final FunctionDecl withOneParam = module.functions().get("withOneParam");
        assertThat(withOneParam.localCount()).isEqualTo(0);
        assertThat(withOneParam.name()).isEqualTo("withOneParam");
        assertThat(withOneParam.parameterCount()).isEqualTo(1);
        final FunctionDecl withTwoParamsAndThreeLocals = module.functions().get("withTwoParamsAndThreeLocals");
        assertThat(withTwoParamsAndThreeLocals.localCount()).isEqualTo(3);
        assertThat(withTwoParamsAndThreeLocals.name()).isEqualTo("withTwoParamsAndThreeLocals");
        assertThat(withTwoParamsAndThreeLocals.parameterCount()).isEqualTo(2);
        final FunctionDecl withThreeParamsAndOneLocal = module.functions().get("withThreeParamsAndOneLocal");
        assertThat(withThreeParamsAndOneLocal.localCount()).isEqualTo(1);
        assertThat(withThreeParamsAndOneLocal.name()).isEqualTo("withThreeParamsAndOneLocal");
        assertThat(withThreeParamsAndOneLocal.parameterCount()).isEqualTo(3);
    }

    @Test
    public void mixedFunctionsAndDeclStmts() {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final YLangParser.ProgramContext ast = compiler.compileToAst("""
                globA := 1
                fn func1() {
                }
                globB := 2
                fn func2() {
                }
                if true {
                    // some body stmt
                }
                return nil
                """, errors);
        assertThat(ast).isNotNull();
        assertThat(errors).isEmpty();
        final ModuleVisitor visitor = new ModuleVisitor();
        final ModuleDecl module = ast.accept(visitor);
        assertThat(module).isNotNull();
        assertThat(visitor.semanticErrors()).isEmpty();
        assertThat(module.mainBody()).isNotNull();
        assertThat(module.mainBody().localCount()).isEqualTo(2);
        assertThat(module.functions()).hasSize(2);
        assertThat(module.functions().keySet()).containsOnly("func1", "func2");
    }

    @Test
    public void errorOnFunctionDeclInBody() {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final YLangParser.ProgramContext ast = compiler.compileToAst("""
                globA := 1
                fn func1() {
                }
                globB := 2
                globA = globB + globA // main body begins here
                fn func2() {
                    // this function decl is not allowed in main body
                }
                return nil
                """, errors);
        assertThat(ast).isNotNull();
        assertThat(errors).isEmpty();
        final ModuleVisitor visitor = new ModuleVisitor();
        final ModuleDecl module = ast.accept(visitor);
        assertThat(module).isNull();
        assertThat(visitor.semanticErrors())
                .hasSize(1)
                .allMatch(msg -> msg.contains("function declaration"));
    }
}
