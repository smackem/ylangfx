package net.smackem.ylang.execution.functions;

import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.lang.FunctionTable;
import net.smackem.ylang.runtime.Value;
import net.smackem.ylang.runtime.ValueType;

import java.util.*;

public enum FunctionRegistry implements FunctionTable {
    INSTANCE;

    private final Map<String, FunctionGroup> repository = new HashMap<>();

    static final String FUNCTION_NAME_SET_AT = "setAt";

    FunctionRegistry() {
        SpecialFunctions.register(this);
        RgbFunctions.register(this);
        ImageFunctions.register(this);
        GeometryFunctions.register(this);
        MathFunctions.register(this);
        UtilFunctions.register(this);
        CollectionFunctions.register(this);
        CommonFunctions.register(this);
    }

    void put(FunctionGroup functionGroup) {
        this.repository.put(functionGroup.name(), functionGroup);
    }

    @Override
    public String indexAssignmentFunction() {
        return FUNCTION_NAME_SET_AT;
    }

    @Override
    public boolean containsFunction(String name) {
        final FunctionGroup fg = this.repository.get(name);
        return fg != null && fg.overloads().stream().anyMatch(overload -> overload.isMethod() == false);
    }

    @Override
    public boolean containsMethod(String name) {
        final FunctionGroup fg = this.repository.get(name);
        return fg != null && fg.overloads().stream().anyMatch(FunctionOverload::isMethod);
    }

    public Value invoke(String name, List<Value> values) throws MissingOverloadException {
        return getFunc(name, values).invoke(values);
    }

    public Func getFunc(String name, List<Value> values) throws MissingOverloadException {
        final FunctionGroup fg = this.repository.get(name);
        if (fg == null) {
            throw new MissingOverloadException("no function with this name found: " + name);
        }
        final List<ValueType> parameters = new ArrayList<>(values.size());
        for (final Value v : values) {
            parameters.add(v.type());
        }
        return fg.get(parameters);
    }

    public String generateDocs() {
        final DocMarker marker = new DocMarker(this.repository, Set.of(FUNCTION_NAME_SET_AT));
        return marker.generateDocs();
    }
}
