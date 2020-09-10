package net.smackem.ylang.execution;

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
        if (this.commutative) {
            this.functions[right.index()][right.index()] = op.swap();
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
