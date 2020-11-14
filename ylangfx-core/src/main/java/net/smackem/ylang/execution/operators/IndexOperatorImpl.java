package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.Context;
import net.smackem.ylang.runtime.*;

import java.util.ArrayList;
import java.util.List;

public class IndexOperatorImpl extends BinaryOperatorImpl {
    IndexOperatorImpl() {
        super(false);
        implement(ValueType.IMAGE, ValueType.POINT, IndexOperatorImpl::imageAtPoint);
        implement(ValueType.LIST, ValueType.NUMBER, IndexOperatorImpl::listAtNumber);
        implement(ValueType.KERNEL, ValueType.NUMBER, IndexOperatorImpl::kernelAtNumber);
        implement(ValueType.KERNEL, ValueType.POINT, IndexOperatorImpl::kernelAtPoint);
        implementLeft(ValueType.MAP, IndexOperatorImpl::mapAtAny);
        implement(ValueType.LIST, ValueType.RANGE, IndexOperatorImpl::listAtRange);
    }

    private static Value listAtRange(Value l, Value r) {
        final ListVal list = (ListVal) l;
        final RangeVal range = (RangeVal) r;
        final List<Value> values = new ArrayList<>();
        for (final Value v : range) {
            final int i = (int) ((NumberVal) v).value();
            values.add(list.get(i));
        }
        return new ListVal(values);
    }

    private static Value mapAtAny(Value l, Value r) {
        final Value v = ((MapVal) l).entries().get(r);
        return v != null ? v : NilVal.INSTANCE;
    }

    private static Value imageAtPoint(Value l, Value r) {
        final var pt = (PointVal) r;
        return ((ImageVal) l).get((int)(pt.x() + 0.5f), (int)(pt.y() + 0.5f));
    }

    private static Value listAtNumber(Value l, Value r) {
        final var list = (ListVal) l;
        return list.get((int)((NumberVal) r).value());
    }

    private static Value kernelAtNumber(Value l, Value r) {
        final var kernel = (KernelVal) l;
        return kernel.get((int)((NumberVal) r).value());
    }

    private static Value kernelAtPoint(Value l, Value r) {
        final var kernel = (KernelVal) l;
        final var pt = (PointVal) r;
        return kernel.get((int) pt.x(), (int) pt.y());
    }
}
