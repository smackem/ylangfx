package net.smackem.ylang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Preprocessor {

    private static final Logger log = LoggerFactory.getLogger(Preprocessor.class);
    private static final Pattern includePattern = Pattern.compile("^#include \"(.+)\"$");
    private final String source;
    private final FileProvider fileProvider;
    private final StringBuilder acc;

    public Preprocessor(String source, FileProvider fileProvider) {
        this.source = Objects.requireNonNull(source);
        this.fileProvider = fileProvider;
        this.acc = new StringBuilder();
    }

    public String preprocess() throws IOException {
        try (final BufferedReader reader = new BufferedReader(new StringReader(this.source))) {
            walk(reader);
        }
        return this.acc.toString();
    }

    private void walk(BufferedReader reader) throws IOException {
        int lineNo = 1;
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#")) {
                executeStmt(line);
            } else {
                this.acc.append(line).append(System.lineSeparator());
            }
            lineNo++;
        }
    }

    private void executeStmt(String line) throws IOException {
        final Matcher matcher = includePattern.matcher(line);
        if (matcher.matches()) {
            final String fileName = matcher.group(1);
            try (final BufferedReader nestedReader = this.fileProvider.open(fileName)) {
                walk(nestedReader);
            }
        }
    }
}
