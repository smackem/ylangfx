package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.Context;
import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.runtime.Value;
import net.smackem.ylang.runtime.ValueType;

abstract class UnaryOperatorImpl {
    private final Func[] functions = new Func[ValueType.MAX_INDEX];

    UnaryOperatorImpl() {
    }

    void implement(ValueType valueType, Func op) {
        this.functions[valueType.index()] = op;
    }

    public Value invoke(Context ctx, Value v) throws MissingOverloadException {
        final var func = this.functions[v.type().index()];
        if (func == null) {
            throw new MissingOverloadException(String.format("%s is not implemented for %s",
                    getClass().getName(), v.type()));
        }
        return func.invoke(ctx, v);
    }

    @FunctionalInterface
    public interface Func {
        Value invoke(Context ctx, Value v);
    }
}
