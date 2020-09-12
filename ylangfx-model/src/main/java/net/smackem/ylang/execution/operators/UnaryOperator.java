package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.Context;
import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.runtime.Value;

public enum UnaryOperator {
    NOT(new NotOperatorImpl()),
    NEG(new NegOperatorImpl());

    private final UnaryOperatorImpl impl;

    UnaryOperator(UnaryOperatorImpl impl) {
        this.impl = impl;
    }

    public Value invoke(Context ctx, Value v) throws MissingOverloadException {
        return this.impl.invoke(ctx, v);
    }
}
