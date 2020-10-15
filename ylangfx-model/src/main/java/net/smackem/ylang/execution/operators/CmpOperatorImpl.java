package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.Context;
import net.smackem.ylang.runtime.NumberVal;
import net.smackem.ylang.runtime.Value;
import net.smackem.ylang.runtime.ValueType;

public class CmpOperatorImpl extends BinaryOperatorImpl {
    CmpOperatorImpl() {
        super(true);
        implement(ValueType.NUMBER, ValueType.NUMBER, CmpOperatorImpl::numberCmpNumber);
    }

    private static Value numberCmpNumber(Value l, Value r) {
        final int result = Float.compare(((NumberVal) l).value(), ((NumberVal) r).value());
        if (result < 0) {
            return NumberVal.MINUS_ONE;
        }
        if (result > 0) {
            return NumberVal.ONE;
        }
        return NumberVal.ZERO;
    }
}
