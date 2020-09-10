package net.smackem.ylang.runtime;

public final class NumberValue extends Value {
    private final float value;

    public NumberValue(float value) {
        super(ValueType.NUMBER);
        this.value = value;
    }

    public float value() {
        return this.value;
    }
}
