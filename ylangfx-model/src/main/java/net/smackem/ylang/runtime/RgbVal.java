package net.smackem.ylang.runtime;

import java.util.Objects;

public class RgbVal extends Value {
    private final float r, g, b, a;

    /**
     * Transparent Black
     */
    public static final RgbVal EMPTY = new RgbVal(0, 0, 0, 0);

    public RgbVal(float r, float g, float b, float a) {
        super(ValueType.RGB);
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public float r() {
        return this.r;
    }

    public float g() {
        return this.g;
    }

    public float b() {
        return this.b;
    }

    public float a() {
        return this.a;
    }

    public float r01() {
        return this.r / 255f;
    }

    public float g01() {
        return this.g / 255f;
    }

    public float b01() {
        return this.b / 255f;
    }

    public float a01() {
        return this.a / 255f;
    }

    public RgbVal add(float number) {
        return new RgbVal(this.r + number, this.g + number, this.b + number, this.a);
    }

    public RgbVal add(RgbVal other) {
        return new RgbVal(this.r + other.r, this.g + other.g, this.b + other.b, this.a);
    }

    public RgbVal multiplyWith(float number) {
        return new RgbVal(this.r * number, this.g * number, this.b * number, this.a);
    }

    public RgbVal multiplyWith(RgbVal other) {
        return new RgbVal(r01() * other.r, g01() * other.g, b01() * other.b, this.a);
    }

    public RgbVal invert() {
        return new RgbVal(255f - this.r, 255f - this.g, 255f - this.b, this.a);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RgbVal rgbVal = (RgbVal) o;
        return Float.compare(rgbVal.r, r) == 0 &&
               Float.compare(rgbVal.g, g) == 0 &&
               Float.compare(rgbVal.b, b) == 0 &&
               Float.compare(rgbVal.a, a) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, g, b, a);
    }

    @Override
    public String toString() {
        return "RgbVal{" +
               "r=" + r +
               ", g=" + g +
               ", b=" + b +
               ", a=" + a +
               '}';
    }
}