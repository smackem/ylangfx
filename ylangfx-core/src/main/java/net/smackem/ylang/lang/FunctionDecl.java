package net.smackem.ylang.lang;

import java.util.Objects;

class FunctionDecl {
    private final String name;
    private final int parameterCount;
    private final int localCount;

    private FunctionDecl(String name, int parameterCount, int localCount) {
        this.name = name;
        this.parameterCount = parameterCount;
        this.localCount = localCount;
    }

    public static FunctionDecl function(String name, int parameterCount, int localCount) {
        Objects.requireNonNull(name);
        return new FunctionDecl(name, parameterCount, localCount);
    }

    public static FunctionDecl main(int localCount) {
        return new FunctionDecl(null, 0, localCount);
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
        return this.parameterCount;
    }
}
