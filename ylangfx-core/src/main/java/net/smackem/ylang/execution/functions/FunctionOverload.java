package net.smackem.ylang.execution.functions;

import net.smackem.ylang.runtime.ValueType;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

class FunctionOverload {
    private final boolean method;
    private final List<ValueType> parameters;
    private final Func func;

    private FunctionOverload(List<ValueType> parameters, Func func, boolean method) {
        Objects.requireNonNull(parameters);
        Objects.requireNonNull(func);
        if (method && parameters.size() == 0) {
            throw new IllegalArgumentException("cannot create a method overload without parameters");
        }
        this.parameters = parameters;
        this.func = func;
        this.method = method;
    }

    public static FunctionOverload function(List<ValueType> parameters, Func func) {
        return new FunctionOverload(parameters, func, false);
    }

    public static FunctionOverload method(List<ValueType> parameters, Func func) {
        return new FunctionOverload(parameters, func, true);
    }

    public boolean isMethod() {
        return this.method;
    }

    public List<ValueType> parameters() {
        return this.parameters;
    }

    public Func func() {
        return this.func;
    }
}
