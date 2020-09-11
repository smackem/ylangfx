package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.Context;
import net.smackem.ylang.runtime.*;

public class IndexOperatorImpl extends BinaryOperatorImpl {
    IndexOperatorImpl() {
        super(false);
        implement(ValueType.IMAGE, ValueType.POINT, IndexOperatorImpl::imageAtPoint);
    }

    private static Value imageAtPoint(Context ctx, Value l, Value r) {
        final var pt = (PointVal) r;
        return ((ImageVal) l).getPixel((int)(pt.x() + 0.5f), (int)(pt.y() + 0.5f));
    }
}
