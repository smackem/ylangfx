package net.smackem.ylang.runtime;

public final class PointVal extends Value {
    private final float x;
    private final float y;

    public PointVal(float x, float y) {
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

    public PointVal translate(PointVal other) {
        return new PointVal(this.x + other.x, this.y + other.y);
    }

    public PointVal offset(float offset) {
        return new PointVal(this.x + offset, this.y + offset);
    }

    public PointVal negate() {
        return new PointVal(-this.x, -this.y);
    }

    public PointVal scale(float number) {
        return new PointVal(this.x * number, this.y * number);
    }

    public PointVal scale(PointVal other) {
        return new PointVal(this.x * other.x, this.y * other.y);
    }
}
