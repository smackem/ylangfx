package net.smackem.ylang.lang;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public final class FunctionDecl {
    private final String name;
    private final int localCount;
    private final Collection<String> parameters;
    private final String docComment;

    private FunctionDecl(String name, Collection<String> parameters, int localCount, String docComment) {
        this.name = name;
        this.localCount = localCount;
        this.parameters = Collections.unmodifiableCollection(Objects.requireNonNull(parameters));
        this.docComment = docComment;
    }

    static FunctionDecl function(String name, Collection<String> parameters, int localCount, String docComment) {
        Objects.requireNonNull(name);
        return new FunctionDecl(name, parameters, localCount, docComment);
    }

    static FunctionDecl main(int localCount) {
        return new FunctionDecl(null, Collections.emptyList(), localCount, "");
    }

    public boolean isMain() {
        return this.name == null;
    }

    public String name() {
        return this.name;
    }

    public int localCount() {
        return this.localCount;
    }

    public int parameterCount() {
        return this.parameters.size();
    }

    public Collection<String> parameters() {
        return this.parameters;
    }

    public String docComment() {
        return this.docComment;
    }
}
