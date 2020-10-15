package net.smackem.ylang.execution.functions;

import net.smackem.ylang.runtime.NumberVal;
import net.smackem.ylang.runtime.Value;
import net.smackem.ylang.runtime.ValueType;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class UtilFunctions {
    private UtilFunctions() {}

    static void register(FunctionRegistry registry) {
        registry.put(new FunctionGroup("random",
                FunctionOverload.function(List.of(ValueType.NUMBER, ValueType.NUMBER), UtilFunctions::integerRandom),
                FunctionOverload.function(Collections.emptyList(), UtilFunctions::floatRandom)));
    }

    private static Value floatRandom(List<Value> args) {
        return new NumberVal(ThreadLocalRandom.current().nextFloat());
    }

    private static Value integerRandom(List<Value> args) {
        return new NumberVal(ThreadLocalRandom.current().nextInt((int) ((NumberVal) args.get(0)).value(), (int) ((NumberVal) args.get(1)).value()));
    }
}
