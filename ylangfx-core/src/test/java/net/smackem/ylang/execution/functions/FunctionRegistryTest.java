package net.smackem.ylang.execution.functions;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

import static org.junit.Assert.*;

public class FunctionRegistryTest {

    @Test
    public void generateDocs() throws IOException {
        final String doc = FunctionRegistry.INSTANCE.generateDocs();
        Files.writeString(Path.of(System.getProperty("user.home"), "ylangfx.html"), doc);
    }
}