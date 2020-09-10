package net.smackem.ylang.execution;

import net.smackem.ylang.runtime.NumberValue;
import net.smackem.ylang.runtime.PointValue;
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
        final float ln = ((NumberValue) l).value();
        final float rn = ((NumberValue) r).value();
        return new NumberValue(ln - rn);
    }

    private static Value pointMinusNumber(Context ctx, Value l, Value r) {
        return ((PointValue) l).offset(-((NumberValue) r).value());
    }

    private static Value pointMinusPoint(Context ctx, Value l, Value r) {
        return ((PointValue) l).translate(((PointValue) r).negate());
    }
}
