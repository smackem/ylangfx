package net.smackem.ylang.model;

import net.smackem.ylang.lang.FileProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class ScriptLibrary implements FileProvider {
    private static final Logger log = LoggerFactory.getLogger(ScriptLibrary.class);
    private final Path basePath;

    private ScriptLibrary(Path basePath) {
        this.basePath = Objects.requireNonNull(basePath);
        if (Files.isDirectory(basePath) == false) {
            throw new IllegalArgumentException(basePath + " is not a directory");
        }
    }

    public static ScriptLibrary fromDirectory(String directory) throws IOException {
        final Path basePath = Paths.get(directory, "ylangfx", "lib");
        return new ScriptLibrary(Files.createDirectories(basePath));
    }

    public Path basePath() {
        return this.basePath;
    }

    public void browse() {
        final Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(this.basePath.toFile());
        } catch (IOException e) {
            log.error("error opening base directory " + this.basePath + ":", e);
        }
    }

    public Collection<Path> scriptFiles() throws IOException {
        return Files.walk(this.basePath, 1, FileVisitOption.FOLLOW_LINKS)
                .filter(p -> p.toString().endsWith(".ylang"))
                .collect(Collectors.toList());
    }

    @Override
    public BufferedReader open(String fileName) throws IOException {
        if (fileName.endsWith(".ylang") == false) {
            fileName += ".ylang";
        }
        final Path path = Paths.get(this.basePath.toString(), fileName);
        return Files.newBufferedReader(path);
    }
}
