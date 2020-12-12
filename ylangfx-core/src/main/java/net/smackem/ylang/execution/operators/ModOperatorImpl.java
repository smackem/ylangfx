package net.smackem.ylang.execution.operators;

import net.smackem.ylang.runtime.*;

public class ModOperatorImpl extends BinaryOperatorImpl {
    ModOperatorImpl() {
        super(false);
        implement(ValueType.NUMBER, ValueType.NUMBER, ModOperatorImpl::numberModNumber);
        implement(ValueType.RGB, ValueType.RGB, ModOperatorImpl::rgbModRgb);
        implement(ValueType.IMAGE, ValueType.IMAGE, ModOperatorImpl::imageModImage);
        implement(ValueType.KERNEL, ValueType.KERNEL, ModOperatorImpl::kernelModKernel);
    }

    private static Value kernelModKernel(Value l, Value r) {
        return ((KernelVal) l).modulo((KernelVal) r);
    }

    private static Value imageModImage(Value l, Value r) {
        return ((ImageVal) l).modulo((ImageVal) r);
    }

    private static Value rgbModRgb(Value l, Value r) {
        return ((RgbVal) l).modulo((RgbVal) r);
    }

    private static Value numberModNumber(Value l, Value r) {
        return new NumberVal(((NumberVal) l).value() % ((NumberVal) r).value());
    }
}
