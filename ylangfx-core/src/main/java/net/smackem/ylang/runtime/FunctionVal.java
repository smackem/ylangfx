package net.smackem.ylang.runtime;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class FunctionVal extends Value {

    private final Function<List<Value>, Value> invocation;
    private final String name;

    public FunctionVal(String name, Function<List<Value>, Value> invocation) {
        super(ValueType.FUNCTION);
        this.invocation = Objects.requireNonNull(invocation);
        this.name = Objects.requireNonNull(name);
    }

    public Value invoke(List<Value> args) {
        return this.invocation.apply(args);
    }

    public String name() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final FunctionVal that = (FunctionVal) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "FunctionVal{" +
               "name='" + name + '\'' +
               '}';
    }
}
