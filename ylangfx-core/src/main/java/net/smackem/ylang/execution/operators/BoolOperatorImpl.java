package net.smackem.ylang.execution.operators;

import net.smackem.ylang.runtime.*;

public class BoolOperatorImpl extends UnaryOperatorImpl {
    BoolOperatorImpl() {
        implement(ValueType.BOOLEAN, BoolOperatorImpl::ofBool);
        implement(ValueType.NUMBER, BoolOperatorImpl::ofNumber);
        implement(ValueType.STRING, BoolOperatorImpl::ofString);
    }

    private static Value ofBool(Value v) {
        return v;
    }

    private static Value ofNumber(Value v) {
        return BoolVal.of(((NumberVal) v).value() != 0f);
    }

    private static Value ofString(Value v) {
        return BoolVal.of(((StringVal) v).isEmpty() == false);
    }
}
