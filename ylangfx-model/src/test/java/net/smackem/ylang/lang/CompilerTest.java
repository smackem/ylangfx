package net.smackem.ylang.lang;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CompilerTest {
    @Test
    public void simple() {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compile("""
                a := 1
                b := 2
                c := a + b
                """, errors);
        assertThat(program).isNotNull();
        System.out.println(program.toString());
    }
}