package net.smackem.ylang.runtime;

import java.util.Objects;

public class FunctionVal extends Value {

    private final int pc;
    private final String name;

    public FunctionVal(int pc, String name) {
        super(ValueType.FUNCTION);
        if (pc < 0) {
            throw new IllegalArgumentException("pc must be zero or positive");
        }
        this.pc = pc;
        this.name = Objects.requireNonNull(name);
    }

    public int pc() {
        return this.pc;
    }

    public String name() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final FunctionVal that = (FunctionVal) o;
        return pc == that.pc &&
               Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pc, name);
    }

    @Override
    public String toString() {
        return "FunctionVal{" +
               "pc=" + pc +
               ", name='" + name + '\'' +
               '}';
    }
}
