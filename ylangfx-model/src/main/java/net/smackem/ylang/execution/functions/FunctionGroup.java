package net.smackem.ylang.execution.functions;

import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.runtime.ValueType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class FunctionGroup {
    private final String name;
    private final Map<List<ValueType>, FunctionOverload> overloads = new HashMap<>();

    public FunctionGroup(String name, FunctionOverload... overloads) {
        this.name = name;
        for (final FunctionOverload overload : overloads) {
            put(overload);
        }
    }

    public String name() {
        return this.name;
    }

    private void put(FunctionOverload overload) {
        if (this.overloads.put(overload.parameters(), overload) != null) {
            throw new IllegalArgumentException(this.name + ": overload already exists: " + overload.parameters());
        }
    }

    public Func get(List<ValueType> parameters) throws MissingOverloadException {
        final FunctionOverload overload = this.overloads.get(parameters);
        if (overload == null) {
            throw new MissingOverloadException(this.name + ": no overload found that matches parameters: " + parameters);
        }
        return overload.func();
    }
}
