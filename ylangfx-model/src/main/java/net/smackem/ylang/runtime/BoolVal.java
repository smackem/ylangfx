package net.smackem.ylang.runtime;

public class BoolVal extends Value {
    private final boolean value;

    public static final BoolVal TRUE = new BoolVal(true);
    public static final BoolVal FALSE = new BoolVal(false);

    private BoolVal(boolean value) {
        super(ValueType.BOOLEAN);
        this.value = value;
    }

    public static BoolVal of(boolean value) {
        return value ? TRUE : FALSE;
    }

    public boolean value() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BoolVal that = (BoolVal) o;
        return this.value == that.value;
    }

    @Override
    public int hashCode() {
        return this.value ? 1 : 0;
    }

    @Override
    public String toString() {
        return "BoolVal{" +
               "value=" + value +
               '}';
    }
}
