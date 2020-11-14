package net.smackem.ylang.execution.operators;

import net.smackem.ylang.runtime.*;

public class MulOperatorImpl extends BinaryOperatorImpl {
    MulOperatorImpl() {
        super(true);
        implement(ValueType.NUMBER, ValueType.NUMBER, MulOperatorImpl::numberTimesNumber);
        implement(ValueType.POINT, ValueType.NUMBER, MulOperatorImpl::pointTimesNumber);
        implement(ValueType.POINT, ValueType.POINT, MulOperatorImpl::pointTimesPoint);
        implement(ValueType.RGB, ValueType.NUMBER, MulOperatorImpl::rgbTimesNumber);
        implement(ValueType.RGB, ValueType.RGB, MulOperatorImpl::rgbTimesRgb);
        implement(ValueType.IMAGE, ValueType.IMAGE, MulOperatorImpl::imageTimesImage);
        implement(ValueType.KERNEL, ValueType.KERNEL, MulOperatorImpl::kernelTimesKernel);
    }

    private static Value kernelTimesKernel(Value l, Value r) {
        return ((KernelVal) l).multiply((KernelVal) r);
    }

    private static Value imageTimesImage(Value l, Value r) {
        return ((ImageVal) l).multiply((ImageVal) r);
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
