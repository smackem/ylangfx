package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.Context;
import net.smackem.ylang.runtime.*;

public class InOperatorImpl extends BinaryOperatorImpl {
    InOperatorImpl() {
        super(false);
        implement(ValueType.POINT, ValueType.RECT, InOperatorImpl::pointInRect);
        implement(ValueType.POINT, ValueType.IMAGE, InOperatorImpl::pointInImage);
        implement(ValueType.NUMBER, ValueType.KERNEL, InOperatorImpl::numberInKernel);
        implementRight(ValueType.LIST, InOperatorImpl::anyInList);
        implement(ValueType.NUMBER, ValueType.RANGE, InOperatorImpl::numberInRange);
        // pointInCircle
        // circleInRect
        // rectInCircle
        // polygonInRect
        // polygonInCircle
    }

    private static Value numberInRange(Value l, Value r) {
        final var n = (NumberVal) l;
        final var range = (RangeVal) r;
        return BoolVal.of(range.contains(n.value()));
    }

    private static Value pointInRect(Value l, Value r) {
        return BoolVal.of(((RectVal) r).contains((PointVal) l));
    }

    private static Value pointInImage(Value l, Value r) {
        final var image = (ImageVal) r;
        final var pt = (PointVal) l;
        return BoolVal.of(pt.x() >= 0 && pt.x() < image.width() &&
                          pt.y() >= 0 && pt.y() < image.height());
    }

    private static Value numberInKernel(Value l, Value r) {
        final var n = (NumberVal) l;
        final var kernel = (KernelVal) r;
        return BoolVal.of(kernel.contains(n));
    }

    private static Value anyInList(Value l, Value r) {
        final var list = (ListVal) r;
        return BoolVal.of(list.contains(l));
    }
}
