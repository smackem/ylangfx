package net.smackem.ylang.lang;

class FunctionDecl {
    private final String name;
    private final int parameterCount;
    private final int localCount;

    public FunctionDecl(String name, int parameterCount, int localCount) {
        this.name = name;
        this.parameterCount = parameterCount;
        this.localCount = localCount;
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
