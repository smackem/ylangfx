package net.smackem.ylang.execution.functions;

import net.smackem.ylang.runtime.Value;
import net.smackem.ylang.runtime.ValueType;

import java.util.List;

class FunctionOverload {
    private final List<ValueType> parameters;
    private final Func func;

    FunctionOverload(List<ValueType> parameters, Func func) {
        this.parameters = parameters;
        this.func = func;
    }

    public List<ValueType> parameters() {
        return this.parameters;
    }

    public Func func() {
        return this.func;
    }
}
