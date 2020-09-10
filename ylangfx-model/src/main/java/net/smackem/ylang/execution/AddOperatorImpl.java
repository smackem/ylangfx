package net.smackem.ylang.execution;

import net.smackem.ylang.runtime.NumberValue;
import net.smackem.ylang.runtime.PointValue;
import net.smackem.ylang.runtime.Value;
import net.smackem.ylang.runtime.ValueType;

class AddOperatorImpl extends BinaryOperatorImpl {
    AddOperatorImpl() {
        super(true);
        implement(ValueType.NUMBER, ValueType.NUMBER, AddOperatorImpl::numberPlusNumber);
        implement(ValueType.POINT, ValueType.NUMBER, AddOperatorImpl::pointPlusNumber);
        implement(ValueType.POINT, ValueType.POINT, AddOperatorImpl::pointPlusPoint);
    }

    private static Value numberPlusNumber(Context ctx, Value l, Value r) {
        final float ln = ((NumberValue) l).value();
        final float rn = ((NumberValue) r).value();
        return new NumberValue(ln + rn);
    }

    private static Value pointPlusNumber(Context ctx, Value l, Value r) {
        return ((PointValue) l).offset(((NumberValue) r).value());
    }

    private static Value pointPlusPoint(Context ctx, Value l, Value r) {
        return ((PointValue) l).translate((PointValue) r);
    }
}
