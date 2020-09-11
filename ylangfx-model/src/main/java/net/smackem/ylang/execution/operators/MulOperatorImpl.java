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

    private static Value numberTimesNumber(Context ctx, Value l, Value r) {
        final float ln = ((NumberVal) l).value();
        final float rn = ((NumberVal) r).value();
        return new NumberVal(ln * rn);
    }

    private static Value pointTimesNumber(Context ctx, Value l, Value r) {
        return ((PointVal) l).scale(((NumberVal) r).value());
    }

    private static Value pointTimesPoint(Context ctx, Value l, Value r) {
        return ((PointVal) l).scale((PointVal) r);
    }

    private static Value rgbTimesNumber(Context ctx, Value l, Value r) {
        return ((RgbVal) l).multiplyWith(((NumberVal) r).value());
    }

    private static Value rgbTimesRgb(Context ctx, Value l, Value r) {
        return ((RgbVal) l).multiplyWith((RgbVal) r);
    }
}
