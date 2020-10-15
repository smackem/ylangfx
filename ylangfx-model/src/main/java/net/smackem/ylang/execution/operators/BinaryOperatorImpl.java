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
            implementRight(left, op.swap());
        }
        final int leftIndex = left.index();
        for (final var right : ValueType.values()) {
            if (right.index() >= 0) {
                this.functions[leftIndex][right.index()] = op;
            }
        }
    }

    void implementRight(ValueType right, Func op) {
        if (this.commutative) {
            implementLeft(right, op.swap());
        }
        final int rightIndex = right.index();
        for (final var left : ValueType.values()) {
            if (left.index() >= 0) {
                this.functions[left.index()][rightIndex] = op;
            }
        }
    }

    public Value invoke(Value l, Value r) throws MissingOverloadException {
        final var func = this.functions[l.type().index()][r.type().index()];
        if (func == null) {
            throw new MissingOverloadException(String.format("%s is not implemented for %s and %s",
                    getClass().getName(), l.type(), r.type()));
        }
        return func.invoke(l, r);
    }

    @FunctionalInterface
    public interface Func {
        Value invoke(Value left, Value right);

        default Func swap() {
            return (l, r) -> this.invoke(r, l);
        }
    }
}
