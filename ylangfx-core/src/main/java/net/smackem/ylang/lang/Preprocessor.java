package net.smackem.ylang.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Preprocessor {

    private static final Logger log = LoggerFactory.getLogger(Preprocessor.class);
    private static final Pattern includePattern = Pattern.compile("^#include \"(.+)\"$");
    private final String source;
    private final FileProvider fileProvider;
    private final StringBuilder acc;
    private final Set<String> visitedIncludes = new HashSet<>();
    private final List<Segment> segments = new ArrayList<>();
    private final Deque<Segment> segmentStack = new ArrayDeque<>();
    private int accLineNumber;
    private Segment currentSegment;

    public Preprocessor(String source, FileProvider fileProvider) {
        this.source = Objects.requireNonNull(source);
        this.fileProvider = fileProvider;
        this.acc = new StringBuilder();
        this.accLineNumber = 1;
    }

    public CodeMap preprocess() throws IOException {
        pushSegment("*");
        try (final BufferedReader reader = new BufferedReader(new StringReader(this.source))) {
            walk(reader);
        }
        return new SegmentedCodeMap(this.acc.toString(), this.segments);
    }

    private void walk(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#")) {
                if (processDirective(line)) {
                    continue;
                }
            }
            appendLine(line);
            this.currentSegment.lineCount++;
        }
    }

    private void pushSegment(String fileName) {
        if (this.currentSegment != null) {
            this.segmentStack.push(this.currentSegment);
        }
        this.currentSegment = new Segment(fileName, this.accLineNumber, 1);
        this.segments.add(this.currentSegment);
    }

    private void popSegment() {
        final Segment topSegment = this.segmentStack.pop();
        this.currentSegment = new Segment(topSegment.fileName,
                this.accLineNumber,
                topSegment.lineNumberOffsetInFile + topSegment.lineCount + 1);
        this.segments.add(this.currentSegment);
    }

    private boolean processDirective(String line) throws IOException {
        final Matcher matcher = includePattern.matcher(line);
        if (matcher.matches()) {
            includeFile(matcher.group(1));
            return true;
        }
        if (line.startsWith("#option")) {
            // ignore option statement
            return false;
        }
        log.warn("unknown preprocessor directive '{}'", line);
        return false;
    }

    private void includeFile(String fileName) throws IOException {
        if (this.visitedIncludes.add(fileName) == false) {
            log.warn("file %s is included multiple times - only including it once".formatted(fileName));
            return;
        }
        pushSegment(fileName);
        try (final BufferedReader nestedReader = this.fileProvider.open(fileName)) {
            walk(nestedReader);
        }
        popSegment();
    }

    private void appendLine(String line) {
        this.acc.append(line).append(System.lineSeparator());
        this.accLineNumber++;
    }

    private static class Segment {
        final String fileName;
        final int lineNumber;
        final int lineNumberOffsetInFile;
        int lineCount;

        Segment(String fileName, int lineNumber, int lineNumberOffsetInFile) {
            this.fileName = fileName;
            this.lineNumber = lineNumber;
            this.lineNumberOffsetInFile = lineNumberOffsetInFile;
        }
    }

    private static class SegmentedCodeMap extends CodeMap {
        final Collection<Segment> segments;

        SegmentedCodeMap(String source, Collection<Segment> segments) {
            super(source);
            this.segments = segments;
        }

        @Override
        public Location translate(int lineNumber) {
            for (final Segment segment : this.segments) {
                if (segment.lineNumber <= lineNumber && lineNumber < segment.lineNumber + segment.lineCount) {
                    return new Location(lineNumber - segment.lineNumber + segment.lineNumberOffsetInFile, segment.fileName);
                }
            }
            return null;
        }
    }
}
