package net.smackem.ylang.lang;

import java.util.Objects;

public abstract class Declaration {
    private final String ident;
    private final String docComment;
    private final int lineNumber;

    Declaration(String ident, String docComment, int lineNumber) {
        this.ident = ident;
        this.docComment = Objects.requireNonNull(docComment);
        this.lineNumber = lineNumber;
    }

    public String ident() {
        return this.ident;
    }

    public String docComment() {
        return this.docComment;
    }

    public int lineNumber() {
        return this.lineNumber;
    }
}
