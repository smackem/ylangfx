package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.Context;
import net.smackem.ylang.runtime.*;

public class InOperatorImpl extends BinaryOperatorImpl {
    InOperatorImpl() {
        super(false);
        implement(ValueType.POINT, ValueType.RECT, InOperatorImpl::pointInRect);
        implement(ValueType.POINT, ValueType.IMAGE, InOperatorImpl::pointInImage);
        // numberInKernel
        // anyInList
        // circleInRect
        // rectInCircle
        // polygonInRect
        // polygonInCircle
    }

    private static Value pointInRect(Context ctx, Value l, Value r) {
        return BoolVal.of(((RectVal) r).contains((PointVal) l));
    }

    private static Value pointInImage(Context ctx, Value l, Value r) {
        final var image = (ImageVal) r;
        final var pt = (PointVal) l;
        return BoolVal.of(pt.x() >= 0 && pt.x() < image.width() &&
                          pt.y() >= 0 && pt.y() < image.height());
    }
}
