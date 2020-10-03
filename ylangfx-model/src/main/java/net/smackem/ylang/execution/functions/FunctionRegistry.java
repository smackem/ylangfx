package net.smackem.ylang.execution.functions;

import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.runtime.NumberVal;
import net.smackem.ylang.runtime.RgbVal;
import net.smackem.ylang.runtime.Value;
import net.smackem.ylang.runtime.ValueType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum FunctionRegistry {
    INSTANCE;

    private final Map<String, FunctionGroup> repository = new HashMap<>();

    public static final String FUNCTION_NAME_SET_AT = "setAt";

    FunctionRegistry() {
        CommonFunctions.register(this);
        RgbFunctions.register(this);
    }

    void put(FunctionGroup functionGroup) {
        this.repository.put(functionGroup.name(), functionGroup);
    }

    public Value invoke(String name, List<Value> values) throws MissingOverloadException {
        final FunctionGroup fg = this.repository.get(name);
        if (fg == null) {
            throw new MissingOverloadException("no function with this name found: " + name);
        }
        final List<ValueType> parameters = values.stream()
                .map(Value::type)
                .collect(Collectors.toList());
        return fg.get(parameters).invoke(values);
    }
}
