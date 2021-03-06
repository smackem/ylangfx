package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.runtime.Value;
import net.smackem.ylang.runtime.ValueType;

abstract class BinaryOperatorImpl {
    private final Func[][] functions = new Func[ValueType.MAX_INDEX][ValueType.MAX_INDEX];
    private final boolean commutative;

    BinaryOperatorImpl(boolean commutative) {
        this.commutative = commutative;
    }

    void implement(ValueType left, ValueType right, Func op) {
        this.functions[left.index()][right.index()] = op;
        if (this.commutative && left != right) {
            this.functions[right.index()][left.index()] = op.swap();
        }
    }

    void implementLeft(ValueType left, Func op) {
        if (this.commutative) {
            implementRightInternal(left, op.swap());
        }
        implementLeftInternal(left, op);
    }

    void implementRight(ValueType right, Func op) {
        if (this.commutative) {
            implementLeftInternal(right, op.swap());
        }
        implementRightInternal(right, op);
    }

    public Value invoke(Value l, Value r) throws MissingOverloadException {
        final var func = this.functions[l.type().index()][r.type().index()];
        if (func == null) {
            throw new MissingOverloadException(String.format("%s is not implemented for %s and %s",
                    getClass().getName(), l.type(), r.type()));
        }
        return func.invoke(l, r);
    }

    private void implementLeftInternal(ValueType left, Func op) {
        final int leftIndex = left.index();
        for (final var right : ValueType.publicValues()) {
            this.functions[leftIndex][right.index()] = op;
        }
    }

    private void implementRightInternal(ValueType right, Func op) {
        final int rightIndex = right.index();
        for (final var left : ValueType.publicValues()) {
            this.functions[left.index()][rightIndex] = op;
        }
    }

    @FunctionalInterface
    public interface Func {
        Value invoke(Value left, Value right);

        default Func swap() {
            return (l, r) -> this.invoke(r, l);
        }
    }
}
