package net.smackem.ylang.execution.functions;

import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.runtime.ValueType;

import java.util.*;

final class FunctionGroup {
    private final String name;
    private final Map<List<ValueType>, FunctionOverload> overloads = new HashMap<>();

    public FunctionGroup(String name, FunctionOverload... overloads) {
        this.name = name;
        for (final FunctionOverload overload : overloads) {
            put(overload);
        }
    }

    public FunctionGroup(String name, Collection<FunctionOverload> overloads) {
        this.name = name;
        for (final FunctionOverload overload : overloads) {
            put(overload);
        }
    }

    public String name() {
        return this.name;
    }

    public Collection<FunctionOverload> overloads() {
        return Collections.unmodifiableCollection(this.overloads.values());
    }

    private void put(FunctionOverload overload) {
        if (this.overloads.put(overload.parameters(), overload) != null) {
            throw new IllegalArgumentException(this.name + ": overload already exists: " + overload.parameters());
        }
    }

    public Func get(List<ValueType> parameters) throws MissingOverloadException {
        FunctionOverload overload = this.overloads.get(parameters);
        if (overload == null) {
            throw new MissingOverloadException(this.name + ": no overload found that matches parameters: " + parameters);
        }
        return overload.func();
    }
}
