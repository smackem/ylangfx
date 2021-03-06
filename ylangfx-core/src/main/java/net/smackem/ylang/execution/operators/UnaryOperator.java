package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.runtime.Value;

public enum UnaryOperator {
    NOT(new NotOperatorImpl()),
    NEG(new NegOperatorImpl()),
    BOOL(new BoolOperatorImpl());

    private final UnaryOperatorImpl impl;

    UnaryOperator(UnaryOperatorImpl impl) {
        this.impl = impl;
    }

    public Value invoke(Value v) throws MissingOverloadException {
        return this.impl.invoke(v);
    }
}
