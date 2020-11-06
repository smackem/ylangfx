package net.smackem.ylang.execution;

import net.smackem.ylang.runtime.ImageVal;

import java.io.Writer;

public final class Context {
    private final Stack stack = new Stack();
    private final ImageVal inputImage;
    private final Writer logWriter;

    Context(ImageVal inputImage, Writer logWriter) {
        this.inputImage = inputImage;
        this.logWriter = logWriter;
    }

    Stack stack() {
        return this.stack;
    }

    public ImageVal inputImage() {
        return this.inputImage;
    }

    public Writer logWriter() {
        return this.logWriter;
    }
}
