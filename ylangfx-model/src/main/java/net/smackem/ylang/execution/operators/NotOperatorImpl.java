package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.Context;
import net.smackem.ylang.runtime.*;

class NotOperatorImpl extends UnaryOperatorImpl {
    NotOperatorImpl() {
        implement(ValueType.BOOLEAN, NotOperatorImpl::notBool);
        implement(ValueType.NUMBER, NotOperatorImpl::notNumber);
        implement(ValueType.STRING, NotOperatorImpl::notString);
    }

    private static Value notBool(Context ctx, Value v) {
        return BoolVal.of(((BoolVal) v).value() == false);
    }

    private static Value notNumber(Context ctx, Value v) {
        return BoolVal.of(((NumberVal) v).value() == 0f);
    }

    private static Value notString(Context ctx, Value v) {
        return BoolVal.of(((StringVal) v).isEmpty());
    }
}
