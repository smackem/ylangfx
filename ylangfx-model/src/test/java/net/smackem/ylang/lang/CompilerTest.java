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
        final Value retVal = new Interpreter(program, null).execute();
        assertThat(retVal).isEqualTo(new NumberVal(3));
        System.out.println(program.toString());
    }

    @Test
    public void compositeAtoms() throws StackException, MissingOverloadException {
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
        final Value retVal = new Interpreter(program, null).execute();
        assertThat(retVal).isEqualTo(new ListVal(List.of(
                new RgbVal(0xff, 0xcc, 0x88, 0x40),
                new KernelVal(List.of(NumberVal.ZERO, NumberVal.ONE, NumberVal.MINUS_ONE, new NumberVal(2))),
                new PointVal(120, 240)
        )));
        System.out.println(program.toString());
    }
}
