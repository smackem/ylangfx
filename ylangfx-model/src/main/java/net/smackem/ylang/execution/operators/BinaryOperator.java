package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.Context;
import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.runtime.Value;

public enum BinaryOperator {
    ADD(new AddOperatorImpl()),
    SUB(new SubOperatorImpl()),
    MUL(new MulOperatorImpl()),
    DIV(new DivOperatorImpl()),
    MOD(new ModOperatorImpl()),
    CMP(new CmpOperatorImpl()),
    INDEX(new IndexOperatorImpl()),
    IN(new InOperatorImpl());

    private final BinaryOperatorImpl impl;

    BinaryOperator(BinaryOperatorImpl impl) {
        this.impl = impl;
    }

    public Value invoke(Context ctx, Value l, Value r) throws MissingOverloadException {
        return this.impl.invoke(ctx, l, r);
    }
}
