package net.smackem.ylang.execution.functions;

import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.runtime.ValueType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class FunctionGroup {
    private final String name;
    private final boolean method;
    private final Map<List<ValueType>, FunctionOverload> overloads = new HashMap<>();

    private FunctionGroup(String name, boolean method, FunctionOverload... overloads) {
        this.name = name;
        this.method = method;
        for (final FunctionOverload overload : overloads) {
            put(overload);
        }
    }

    static FunctionGroup method(String name, FunctionOverload... overloads) {
        return new FunctionGroup(name, true, overloads);
    }

    static FunctionGroup function(String name, FunctionOverload... overloads) {
        return new FunctionGroup(name, false, overloads);
    }

    public String name() {
        return this.name;
    }

    public boolean isMethod() {
        return this.method;
    }

    private void put(FunctionOverload overload) {
        if (this.overloads.put(overload.parameters(), overload) != null) {
            throw new IllegalArgumentException(this.name + ": overload already exists: " + overload.parameters());
        }
        if (this.method && overload.parameters().size() == 0) {
            throw new IllegalArgumentException(this.name + " is a method and all overloads must have at least 1 parameter");
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
