package net.smackem.ylang.execution.operators;

import net.smackem.ylang.execution.Context;
import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.runtime.Value;
import net.smackem.ylang.runtime.ValueType;

public abstract class BinaryOperatorImpl {
    private final Func[][] functions = new Func[ValueType.MAX_INDEX][ValueType.MAX_INDEX];
    private final boolean commutative;

    BinaryOperatorImpl(boolean commutative) {
        this.commutative = commutative;
    }

    void implement(ValueType left, ValueType right, Func op) {
        this.functions[left.index()][right.index()] = op;
        if (this.commutative && left != right) {
            this.functions[right.index()][right.index()] = op.swap();
        }
    }

    void implementLeft(ValueType left, Func op) {
        if (this.commutative) {
            implementRight(left, op.swap());
        }
        final int leftIndex = left.index();
        for (final var right : ValueType.values()) {
            this.functions[leftIndex][right.index()] = op;
        }
    }

    void implementRight(ValueType right, Func op) {
        if (this.commutative) {
            implementLeft(right, op.swap());
        }
        final int rightIndex = right.index();
        for (final var left : ValueType.values()) {
            this.functions[left.index()][rightIndex] = op;
        }
    }

    public Value invoke(Context ctx, Value l, Value r) throws MissingOverloadException {
        final var func = this.functions[l.type().index()][r.type().index()];
        if (func == null) {
            throw new MissingOverloadException("%s is not implemented for %s and %s".formatted(
                    getClass().getName(), l.type(), r.type()));
        }
        return this.functions[l.type().index()][r.type().index()].invoke(ctx, l, r);
    }

    @FunctionalInterface
    public interface Func {
        Value invoke(Context ctx, Value left, Value right);

        default Func swap() {
            return (ctx, l, r) -> this.invoke(ctx, r, l);
        }
    }
}
