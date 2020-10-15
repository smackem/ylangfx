package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.Context;
import net.smackem.ylang.runtime.*;

public class MulOperatorImpl extends BinaryOperatorImpl {
    MulOperatorImpl() {
        super(true);
        implement(ValueType.NUMBER, ValueType.NUMBER, MulOperatorImpl::numberTimesNumber);
        implement(ValueType.POINT, ValueType.NUMBER, MulOperatorImpl::pointTimesNumber);
        implement(ValueType.POINT, ValueType.POINT, MulOperatorImpl::pointTimesPoint);
        implement(ValueType.RGB, ValueType.NUMBER, MulOperatorImpl::rgbTimesNumber);
        implement(ValueType.RGB, ValueType.RGB, MulOperatorImpl::rgbTimesRgb);
    }

    private static Value numberTimesNumber(Value l, Value r) {
        final float ln = ((NumberVal) l).value();
        final float rn = ((NumberVal) r).value();
        return new NumberVal(ln * rn);
    }

    private static Value pointTimesNumber(Value l, Value r) {
        return ((PointVal) l).scale(((NumberVal) r).value());
    }

    private static Value pointTimesPoint(Value l, Value r) {
        return ((PointVal) l).scale((PointVal) r);
    }

    private static Value rgbTimesNumber(Value l, Value r) {
        return ((RgbVal) l).multiplyWith(((NumberVal) r).value());
    }

    private static Value rgbTimesRgb(Value l, Value r) {
        return ((RgbVal) l).multiplyWith((RgbVal) r);
    }
}
