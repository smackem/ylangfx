package net.smackem.ylang.execution.functions;

import net.smackem.ylang.runtime.ValueType;

import java.util.List;
import java.util.Objects;

class FunctionOverload {
    private final boolean method;
    private final List<ValueType> parameters;
    private final Func func;
    private final String doc;

    private FunctionOverload(List<ValueType> parameters, String doc, Func func, boolean method) {
        Objects.requireNonNull(parameters);
        Objects.requireNonNull(func);
        if (method && parameters.size() == 0) {
            throw new IllegalArgumentException("cannot create a method overload without parameters");
        }
        this.parameters = parameters;
        this.func = func;
        this.method = method;
        this.doc = doc;
    }

    public static FunctionOverload function(List<ValueType> parameters, String doc, Func func) {
        return new FunctionOverload(parameters, doc, func, false);
    }

    public static FunctionOverload method(List<ValueType> parameters, String doc, Func func) {
        return new FunctionOverload(parameters, doc, func, true);
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

    public String doc() {
        return this.doc;
    }
}
