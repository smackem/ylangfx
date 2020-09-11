package net.smackem.ylang.runtime;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PointVal pointVal = (PointVal) o;
        return Float.compare(pointVal.x, this.x) == 0 &&
               Float.compare(pointVal.y, this.y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }
}
