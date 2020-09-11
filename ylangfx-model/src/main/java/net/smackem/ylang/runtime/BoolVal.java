package net.smackem.ylang.runtime;

public class BoolVal extends Value {
    private final boolean value;

    public BoolVal(boolean value) {
        super(ValueType.BOOLEAN);
        this.value = value;
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
}
