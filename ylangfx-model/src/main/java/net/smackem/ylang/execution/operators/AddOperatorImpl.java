package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.Context;
import net.smackem.ylang.runtime.*;

class AddOperatorImpl extends BinaryOperatorImpl {
    AddOperatorImpl() {
        super(true);
        implement(ValueType.NUMBER, ValueType.NUMBER, AddOperatorImpl::numberPlusNumber);
        implement(ValueType.POINT, ValueType.NUMBER, AddOperatorImpl::pointPlusNumber);
        implement(ValueType.POINT, ValueType.POINT, AddOperatorImpl::pointPlusPoint);
        implement(ValueType.RGB, ValueType.NUMBER, AddOperatorImpl::rgbPlusNumber);
        implement(ValueType.RGB, ValueType.RGB, AddOperatorImpl::rgbPlusRgb);
        implementLeft(ValueType.STRING, AddOperatorImpl::stringPlusAny);
    }

    private static Value numberPlusNumber(Value l, Value r) {
        final float ln = ((NumberVal) l).value();
        final float rn = ((NumberVal) r).value();
        return new NumberVal(ln + rn);
    }

    private static Value pointPlusNumber(Value l, Value r) {
        return ((PointVal) l).offset(((NumberVal) r).value());
    }

    private static Value pointPlusPoint(Value l, Value r) {
        return ((PointVal) l).translate((PointVal) r);
    }

    private static Value rgbPlusNumber(Value l, Value r) {
        return ((RgbVal) l).add(((NumberVal) r).value());
    }

    private static Value rgbPlusRgb(Value l, Value r) {
        return ((RgbVal) l).add((RgbVal) r);
    }

    private static Value stringPlusAny(Value l, Value r) {
        return new StringVal(((StringVal) l).value() + r.toLangString());
    }
}
