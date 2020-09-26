package net.smackem.ylang.runtime;

import java.util.Iterator;

public class IteratorVal extends Value {
    private final Iterator<Value> it;

    public IteratorVal(Iterator<Value> it) {
        super(ValueType.INFRASTRUCTURE);
        this.it = it;
    }

    public Value next() {
        return it.hasNext() ? it.next() : null;
    }
}
