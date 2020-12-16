package net.smackem.ylang.execution.operators;

import net.smackem.ylang.runtime.*;

public class NegOperatorImpl extends UnaryOperatorImpl {
    NegOperatorImpl() {
        implement(ValueType.NUMBER, NegOperatorImpl::negNumber);
        implement(ValueType.POINT, NegOperatorImpl::negPoint);
        implement(ValueType.RGB, NegOperatorImpl::negRgb);
        implement(ValueType.IMAGE, NegOperatorImpl::negImage);
        implement(ValueType.KERNEL, NegOperatorImpl::negKernel);
    }

    private static Value negKernel(Value value) {
        return ((KernelVal) value).invert();
    }

    private static Value negNumber(Value value) {
        return new NumberVal(-((NumberVal) value).value());
    }

    private static Value negPoint(Value value) {
        return ((PointVal) value).negate();
    }

    private static Value negRgb(Value value) {
        return ((RgbVal) value).invert();
    }

    private static Value negImage(Value value) {
        return ((ImageVal) value).invert();
    }
}
