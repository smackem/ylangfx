package net.smackem.ylang.runtime;

public final class PointValue extends Value {
    private final float x;
    private final float y;

    public PointValue(float x, float y) {
        super(ValueType.POINT);
        this.x = x;
        this.y = y;
    }

    public float x() {
        return this.x;
    }

    public float y() {
        return this.y;
    }

    public PointValue translate(PointValue other) {
        return new PointValue(this.x + other.x, this.y + other.y);
    }

    public PointValue offset(float offset) {
        return new PointValue(this.x + offset, this.y + offset);
    }

    public PointValue negate() {
        return new PointValue(-this.x, -this.y);
    }
}
