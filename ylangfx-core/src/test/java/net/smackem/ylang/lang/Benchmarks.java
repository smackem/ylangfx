package net.smackem.ylang.lang;

import net.smackem.ylang.execution.ExecutionException;
import net.smackem.ylang.execution.Interpreter;
import net.smackem.ylang.execution.functions.FunctionRegistry;
import org.junit.Test;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class Benchmarks {
    private static final int CONVOLVE_REPETITIONS = 50;

    @Test
    //@Ignore
    public void benchmarkConvolveImage() throws ExecutionException {
        final Compiler compiler = new Compiler();
        final List<String> errors = new ArrayList<>();
        final Program program = compiler.compileWithoutPreprocessing("""
                K := kernel(11, 11, 1)
                inp := image(300, 300)
                out := image(inp)
                for i in 0 .. %d {
                    for p in inp.bounds {
                        out[p] = inp.convolve(p, K)
                    }
                }
                return nil
                """.formatted(CONVOLVE_REPETITIONS), FunctionRegistry.INSTANCE, errors);
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
                for i in 0 .. %d {
                    out := inp.convolve(K)
                }
                return nil
                """.formatted(CONVOLVE_REPETITIONS), FunctionRegistry.INSTANCE, errors);
        assertThat(errors).isEmpty();
        assertThat(program).isNotNull();
        System.out.println(program.toString());
        new Interpreter(program, null, Writer.nullWriter()).execute();
    }
}
