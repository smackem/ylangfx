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

    public float intensity() {
        return 0.299f * this.r + 0.587f * this.g + 0.114f * this.b;
    }

    public float intensity01() {
        return intensity() / 255f;
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

    /**
     * expresses the alpha compositing operation "over", painting this {@link RgbVal}
     * over the {@link RgbVal} {@code background}.
     * @param background The background color.
     * @return The result of the alpha compositing operation.
     */
    public RgbVal over(RgbVal background) {
        final float foregroundA = this.a01();
        final float backgroundA = background.a01();
        final float multipliedA = (1f - foregroundA) * backgroundA;
        final float a = backgroundA + (1f - backgroundA) * foregroundA;

        return new RgbVal(
                clamp((this.r * foregroundA + background.r * multipliedA) / a),
                clamp((this.g * foregroundA + background.g * multipliedA) / a),
                clamp((this.b * foregroundA + background.b * multipliedA) / a),
                255f * a);
    }

    public RgbVal clamp() {
        return new RgbVal(clamp(this.r), clamp(this.g), clamp(this.b), clamp(this.a));
    }

    public static float clamp(float channelValue) {
        if (channelValue > 255f) {
            return 255f;
        }
        return Math.max(channelValue, 0f);
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

    @Override
    public String toLangString() {
        return String.format(RuntimeParameters.LOCALE, "rgba(%f, %f, %f, %f)", this.r, this.g, this.b, this.a);
    }
}
