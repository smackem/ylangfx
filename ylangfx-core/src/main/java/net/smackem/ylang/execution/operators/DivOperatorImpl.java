package net.smackem.ylang.execution.operators;

import net.smackem.ylang.runtime.*;

public class DivOperatorImpl extends BinaryOperatorImpl {
    DivOperatorImpl() {
        super(false);
        implement(ValueType.NUMBER, ValueType.NUMBER, DivOperatorImpl::numberDividedByNumber);
        implement(ValueType.RGB, ValueType.NUMBER, DivOperatorImpl::rgbDividedByNumber);
        implement(ValueType.RGB, ValueType.RGB, DivOperatorImpl::rgbDividedByRgb);
        implement(ValueType.IMAGE, ValueType.IMAGE, DivOperatorImpl::imageDividedByImage);
    }

    private static Value imageDividedByImage(Value l, Value r) {
        return ((ImageVal) l).divide((ImageVal) r);
    }

    private static Value numberDividedByNumber(Value l, Value r) {
        return new NumberVal(((NumberVal) l).value() / ((NumberVal) r).value());
    }

    private static Value rgbDividedByNumber(Value l, Value r) {
        final var rgb = (RgbVal) l;
        final var number = ((NumberVal) r).value();
        return new RgbVal(rgb.r() / number, rgb.g() / number, rgb.b() / number, rgb.a());
    }

    private static Value rgbDividedByRgb(Value l, Value r) {
        final var lrgb = (RgbVal) l;
        final var rrgb = ((RgbVal) r);
        return lrgb.divideBy(rrgb);
    }
}
