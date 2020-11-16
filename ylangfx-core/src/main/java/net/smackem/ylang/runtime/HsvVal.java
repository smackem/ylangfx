package net.smackem.ylang.runtime;

import java.util.Objects;

public class HsvVal extends Value {
    private final float hue;
    private final float saturation;
    private final float value;

    public HsvVal(float hue, float saturation, float value) {
        super(ValueType.HSV);
        this.hue = hue;
        this.saturation = saturation;
        this.value = value;
    }

    public float hue() {
        return this.hue;
    }

    public float saturation() {
        return this.saturation;
    }

    public float value() {
        return this.value;
    }

    public static HsvVal fromRgb(RgbVal rgb) {
        final float r = rgb.r01();
        final float g = rgb.g01();
        final float b = rgb.b01();

        final float max = Math.max(r, Math.max(g, b));
        final float min = Math.min(r, Math.min(g, b));

        final float hue;

        if (max == min) {
            hue = 0;
        } else if (max == r && g >= b) {
            hue = 60f * (g - b) / (max - min) + 0f;
        } else if (max == r && g < b) {
            hue = 60f * (g - b) / (max - min) + 360f;
        } else if (max == g) {
            hue = 60f * (b - r) / (max - min) + 120f;
        } else if (max == b) {
            hue = 60f * (r - g) / (max - min) + 240f;
        } else {
            hue = 0f;
        }

        final float s;
        if (max == 0) {
            s = 0f;
        } else {
            s = 1f - min / max;
        }

        return new HsvVal(hue, s, max);
    }

    public HsvVal clamp() {
        float h = this.hue;
        float s = this.saturation;
        float v = this.value;

        if (h >= 360) {
            h = 360 - Float.MIN_VALUE;
        } else if (h < 0) {
            h = 0;
        }

        if (s > 1) {
            s = 1;
        } else if (s < 0) {
            s = 0;
        }

        if (v > 1) {
            v = 1;
        } else if (v < 0) {
            v = 0;
        }

        return new HsvVal(h, s, v);
    }

    public RgbVal toRgb() {
        final HsvVal hsv = clamp();
        final float h = hsv.hue;
        final float s = hsv.saturation;
        final float v = hsv.value;

        final int hi = ((int)h) / 60 % 6;
        final float f = h / 60f - hi;

        float r = 0;
        float g = 0;
        float b = 0;

        final float p = v * (1f - s);
        final float q = v * (1f - f * s);
        final float t = v * (1f - (1f - f) * s);

        switch (hi) {
            case 0 -> {
                r = v;
                g = t;
                b = p;
            }
            case 1 -> {
                r = q;
                g = v;
                b = p;
            }
            case 2 -> {
                r = p;
                g = v;
                b = t;
            }
            case 3 -> {
                r = p;
                g = q;
                b = v;
            }
            case 4 -> {
                r = t;
                g = p;
                b = v;
            }
            case 5 -> {
                r = v;
                g = p;
                b = q;
            }
        }

        return new RgbVal(r * 255, g * 255, b * 255, 255);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HsvVal hsvVal = (HsvVal) o;
        return Float.compare(hsvVal.hue, hue) == 0 &&
                Float.compare(hsvVal.saturation, saturation) == 0 &&
                Float.compare(hsvVal.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hue, saturation, value);
    }

    @Override
    public String toString() {
        return "HsvVal{" +
                "hue=" + hue +
                ", saturation=" + saturation +
                ", value=" + value +
                '}';
    }

    @Override
    public String toLangString() {
        return String.format(RuntimeParameters.LOCALE, "hsv(%f, %f, %f)", this.hue, this.saturation, this.value);
    }
}
