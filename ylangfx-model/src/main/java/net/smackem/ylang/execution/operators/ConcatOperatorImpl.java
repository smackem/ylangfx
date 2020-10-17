package net.smackem.ylang.execution.operators;

import net.smackem.ylang.runtime.ListVal;
import net.smackem.ylang.runtime.Value;
import net.smackem.ylang.runtime.ValueType;

public class ConcatOperatorImpl extends BinaryOperatorImpl {
    ConcatOperatorImpl() {
        super(false);
        implement(ValueType.LIST, ValueType.LIST, ConcatOperatorImpl::concatLists);
    }

    private static Value concatLists(Value l, Value r) {
        return ((ListVal) l).concat((ListVal) r);
    }
}
