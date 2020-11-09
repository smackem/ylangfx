package net.smackem.ylang.runtime;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class FunctionVal extends Value {

    private final Function<List<Value>, Value> invocation;
    private final String name;
    private final int parameterCount;

    public FunctionVal(String name, int parameterCount, Function<List<Value>, Value> invocation) {
        super(ValueType.FUNCTION);
        this.name = Objects.requireNonNull(name);
        this.parameterCount = parameterCount;
        this.invocation = Objects.requireNonNull(invocation);
    }

    public Value invoke(List<Value> args) {
        if (Objects.requireNonNull(args).size() != this.parameterCount) {
            throw new IllegalArgumentException(
                    "function expects %d arguments, only %d were passed".formatted(
                            this.parameterCount, args.size()));
        }
        return this.invocation.apply(args);
    }

    public String name() {
        return this.name;
    }

    public int parameterCount() {
        return this.parameterCount;
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
               "name='" + name + "', " +
               "parameterCount=" + parameterCount +
               '}';
    }
}
