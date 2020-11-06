package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.Context;
import net.smackem.ylang.runtime.*;

public class NegOperatorImpl extends UnaryOperatorImpl {
    NegOperatorImpl() {
        implement(ValueType.NUMBER, NegOperatorImpl::negNumber);
        implement(ValueType.POINT, NegOperatorImpl::negPoint);
        implement(ValueType.RGB, NegOperatorImpl::negRgb);
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
}
