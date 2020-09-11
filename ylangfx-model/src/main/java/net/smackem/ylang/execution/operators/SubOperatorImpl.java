package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.Context;
import net.smackem.ylang.runtime.NumberVal;
import net.smackem.ylang.runtime.PointVal;
import net.smackem.ylang.runtime.Value;
import net.smackem.ylang.runtime.ValueType;

public class SubOperatorImpl extends BinaryOperatorImpl {
    SubOperatorImpl() {
        super(false);
        implement(ValueType.NUMBER, ValueType.NUMBER, SubOperatorImpl::numberMinusNumber);
        implement(ValueType.POINT, ValueType.NUMBER, SubOperatorImpl::pointMinusNumber);
        implement(ValueType.POINT, ValueType.POINT, SubOperatorImpl::pointMinusPoint);
    }

    private static Value numberMinusNumber(Context ctx, Value l, Value r) {
        final float ln = ((NumberVal) l).value();
        final float rn = ((NumberVal) r).value();
        return new NumberVal(ln - rn);
    }

    private static Value pointMinusNumber(Context ctx, Value l, Value r) {
        return ((PointVal) l).offset(-((NumberVal) r).value());
    }

    private static Value pointMinusPoint(Context ctx, Value l, Value r) {
        return ((PointVal) l).translate(((PointVal) r).negate());
    }
}
