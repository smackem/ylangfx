package net.smackem.ylang.execution;

import net.smackem.ylang.runtime.ImageVal;

public final class Context {
    private final Stack stack = new Stack();
    private final ImageVal inputImage;

    public static final Context EMPTY = new Context(null);

    Context(ImageVal inputImage) {
        this.inputImage = inputImage;
    }

    Stack stack() {
        return this.stack;
    }

    public ImageVal inputImage() {
        return this.inputImage;
    }
}
