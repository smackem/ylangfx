package net.smackem.ylang.runtime;

import java.util.Locale;

public final class NumberVal extends Value {
    private final float value;

    public final static NumberVal MINUS_ONE = new NumberVal(-1);
    public final static NumberVal ZERO = new NumberVal(0);
    public final static NumberVal ONE = new NumberVal(1);

    public NumberVal(float value) {
        super(ValueType.NUMBER);
        this.value = value;
    }

    public float value() {
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
        final NumberVal numberVal = (NumberVal) o;
        return Float.compare(numberVal.value, this.value) == 0;
    }

    @Override
    public int hashCode() {
        return this.value != +0.0f ? Float.floatToIntBits(this.value) : 0;
    }

    @Override
    public String toString() {
        return "NumberVal{" +
               "value=" + value +
               '}';
    }

    @Override
    public String toLangString() {
        return String.format(RuntimeParameters.LOCALE, "%f", this.value);
    }
}
