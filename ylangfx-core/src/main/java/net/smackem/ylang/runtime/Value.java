package net.smackem.ylang.runtime;

public abstract class Value {
    private final ValueType type;

    Value(ValueType type) {
        this.type = type;
    }

    public final ValueType type() {
        return this.type;
    }

    public String toLangString() {
        return toString();
    }
}
