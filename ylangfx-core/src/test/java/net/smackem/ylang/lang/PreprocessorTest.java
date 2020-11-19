package net.smackem.ylang.lang;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;

public class PreprocessorTest {
    @Test
    public void preprocessWithoutStmts() throws IOException {
        final String source = """
                line 1
                line 2
                line 3
                """;
        final Preprocessor preprocessor = new Preprocessor(source, createFileProvider());
        final String accSource = preprocessor.preprocess();
        assertThat(accSource).isEqualTo(source);
    }

    @Test
    public void singleStmt() throws IOException {
        final String source = """
                line 1
                line 2
                #include "inc-1"
                line 3
                """;
        final Preprocessor preprocessor = new Preprocessor(source, createFileProvider());
        final String accSource = preprocessor.preprocess();
        assertThat(accSource).isEqualTo("""
                line 1
                line 2
                hello from inc-1
                line 3
                """);
    }

    @Test
    public void multilineStmt() throws IOException {
        final String source = """
                line 1
                line 2
                #include "inc-2"
                line 3
                """;
        final Preprocessor preprocessor = new Preprocessor(source, createFileProvider());
        final String accSource = preprocessor.preprocess();
        assertThat(accSource).isEqualTo("""
                line 1
                line 2
                hello from inc-2

                line 3
                """);
    }

    @Test
    public void multipleStmts() throws IOException {
        final String source = """
                line 1
                line 2
                #include "inc-1"
                #include "inc-2"
                line 3
                """;
        final Preprocessor preprocessor = new Preprocessor(source, createFileProvider());
        final String accSource = preprocessor.preprocess();
        assertThat(accSource).isEqualTo("""
                line 1
                line 2
                hello from inc-1
                hello from inc-2

                line 3
                """);
    }

    @Test
    public void nestedInclude() throws IOException {
        final String source = """
                line 1
                line 2
                #include "inc-3"
                line 3
                """;
        final Preprocessor preprocessor = new Preprocessor(source, createFileProvider());
        final String accSource = preprocessor.preprocess();
        assertThat(accSource).isEqualTo("""
                line 1
                line 2
                hello from inc-3
                hello from inc-1
                end of inc-3
                line 3
                """);
    }

    @Test
    public void circularInclude() throws IOException {
        final String source = """
                line 1
                line 2
                #include "inc-4"
                line 3
                """;
        final Preprocessor preprocessor = new Preprocessor(source, createFileProvider());
        final String accSource = preprocessor.preprocess();
        assertThat(accSource).isEqualTo("""
                line 1
                line 2
                hello from inc-4
                hello from inc-5
                line 3
                """);
    }

    private FileProvider createFileProvider() {
        return fileName -> {
            final String includedSource = switch (fileName) {
                case "inc-1" -> "hello from inc-1";
                case "inc-2" -> """
                        hello from inc-2
                        
                        """;
                case "inc-3" -> """
                        hello from inc-3
                        #include "inc-1"
                        end of inc-3
                        """;
                case "inc-4" -> """
                        hello from inc-4
                        #include "inc-5"
                        """;
                case "inc-5" -> """
                        hello from inc-5
                        #include "inc-4"
                        """;
                default -> throw new IllegalArgumentException("include not found");
            };
            return new BufferedReader(new StringReader(includedSource));
        };
    }
}