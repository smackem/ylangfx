package net.smackem.ylang.execution.operators;

import net.smackem.ylang.runtime.*;

public class SubOperatorImpl extends BinaryOperatorImpl {
    SubOperatorImpl() {
        super(false);
        implement(ValueType.NUMBER, ValueType.NUMBER, SubOperatorImpl::numberMinusNumber);
        implement(ValueType.POINT, ValueType.NUMBER, SubOperatorImpl::pointMinusNumber);
        implement(ValueType.POINT, ValueType.POINT, SubOperatorImpl::pointMinusPoint);
        implement(ValueType.RGB, ValueType.RGB, SubOperatorImpl::rgbMinusRgb);
        implement(ValueType.RGB, ValueType.NUMBER, SubOperatorImpl::rgbMinusNumber);
    }

    private static Value rgbMinusNumber(Value l, Value r) {
        final RgbVal lc = (RgbVal) l;
        final float rn = ((NumberVal) r).value();
        return new RgbVal(lc.r() - rn, lc.g() - rn, lc.b() - rn, lc.a());
    }

    private static Value rgbMinusRgb(Value l, Value r) {
        final RgbVal lc = (RgbVal) l;
        final RgbVal rc = (RgbVal) r;
        return new RgbVal(lc.r() - rc.r(), lc.g() - rc.g(), lc.b() - rc.b(), lc.a());
    }

    private static Value numberMinusNumber(Value l, Value r) {
        final float ln = ((NumberVal) l).value();
        final float rn = ((NumberVal) r).value();
        return new NumberVal(ln - rn);
    }

    private static Value pointMinusNumber(Value l, Value r) {
        return ((PointVal) l).offset(-((NumberVal) r).value());
    }

    private static Value pointMinusPoint(Value l, Value r) {
        return ((PointVal) l).translate(((PointVal) r).negate());
    }
}
