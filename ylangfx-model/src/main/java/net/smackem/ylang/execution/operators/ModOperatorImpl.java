package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.Context;
import net.smackem.ylang.runtime.NumberVal;
import net.smackem.ylang.runtime.Value;

public class ModOperatorImpl extends BinaryOperatorImpl {
    ModOperatorImpl() {
        super(false);
    }

    private static Value numberModNumber(Context ctx, Value l, Value r) {
        return new NumberVal(((NumberVal) l).value() % ((NumberVal) r).value());
    }
}
