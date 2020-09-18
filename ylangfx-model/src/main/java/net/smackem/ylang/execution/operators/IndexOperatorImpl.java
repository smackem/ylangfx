package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.Context;
import net.smackem.ylang.runtime.*;

public class IndexOperatorImpl extends BinaryOperatorImpl {
    IndexOperatorImpl() {
        super(false);
        implement(ValueType.IMAGE, ValueType.POINT, IndexOperatorImpl::imageAtPoint);
        implement(ValueType.LIST, ValueType.NUMBER, IndexOperatorImpl::listAtNumber);
        implement(ValueType.KERNEL, ValueType.NUMBER, IndexOperatorImpl::kernelAtNumber);
        implement(ValueType.KERNEL, ValueType.POINT, IndexOperatorImpl::kernelAtPoint);
    }

    private static Value imageAtPoint(Context ctx, Value l, Value r) {
        final var pt = (PointVal) r;
        return ((ImageVal) l).getPixel((int)(pt.x() + 0.5f), (int)(pt.y() + 0.5f));
    }

    private static Value listAtNumber(Context ctx, Value l, Value r) {
        final var list = (ListVal) l;
        return list.get((int)((NumberVal) r).value());
    }

    private static Value kernelAtNumber(Context ctx, Value l, Value r) {
        final var kernel = (KernelVal) l;
        return kernel.get((int)((NumberVal) r).value());
    }

    private static Value kernelAtPoint(Context ctx, Value l, Value r) {
        final var kernel = (KernelVal) l;
        final var pt = (PointVal) r;
        return kernel.get((int) pt.x(), (int) pt.y());
    }
}
