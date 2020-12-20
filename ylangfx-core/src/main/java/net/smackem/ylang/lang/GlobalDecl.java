package net.smackem.ylang.lang;

import java.util.Objects;

public final class GlobalDecl {
    private final String name;
    private final String docComment;

    GlobalDecl(String name, String docComment) {
        this.name = Objects.requireNonNull(name);
        this.docComment = docComment;
    }

    public String name() {
        return this.name;
    }

    public String docComment() {
        return this.docComment;
    }
}
