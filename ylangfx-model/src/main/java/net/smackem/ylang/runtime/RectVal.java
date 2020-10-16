package net.smackem.ylang.runtime;

import java.util.Iterator;
import java.util.Objects;

public class RectVal extends GeometryVal {
    private final float x, y, width, height;

    public static final RectVal EMPTY = new RectVal(0f, 0f, 0f, 0f);

    public RectVal(float x, float y, float width, float height) {
        super(ValueType.RECT);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public static RectVal fromIntRect(IntRect rc) {
        return new RectVal(rc.left(), rc.top(), rc.right() - rc.left(), rc.bottom() - rc.top());
    }

    public IntRect round() {
        return new IntRect((int) (this.x + 0.5f), (int) (this.y + 0.5f), (int) (right() + 0.5f), (int) (bottom() + 0.5f));
    }

    public float x() {
        return this.x;
    }

    public float y() {
        return this.y;
    }

    public float width() {
        return this.width;
    }

    public float height() {
        return this.height;
    }

    public float right() {
        return this.x + this.width;
    }

    public float bottom() {
        return this.y + this.height;
    }

    public RectVal inflate(float dx, float dy) {
        return new RectVal(this.x - dx, this.y - dy, this.width + dx * 2, this.height + dx * 2);
    }

    public boolean contains(PointVal pt) {
        return pt.x() >= this.x && pt.x() < this.right() &&
                pt.y() >= this.y && pt.y() < this.bottom();
    }

    @Override
    public GeometryVal translate(PointVal pt) {
        return new RectVal(this.x + pt.x(), this.y + pt.y(), this.width, this.height);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RectVal rectVal = (RectVal) o;
        return Float.compare(rectVal.x, x) == 0 &&
               Float.compare(rectVal.y, y) == 0 &&
               Float.compare(rectVal.width, width) == 0 &&
               Float.compare(rectVal.height, height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, width, height);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Iterator<Value> iterator() {
        return new PointIterator();
    }

    @Override
    public String toLangString() {
        return String.format(RuntimeParameters.LOCALE, "rect(%f;%f, %f, %f)", this.x, this.y, this.width, this.height);
    }

    private class PointIterator implements Iterator<Value> {
        int x, y;

        @Override
        public boolean hasNext() {
            return this.y < height;
        }

        @Override
        public Value next() {
            final var pt = new PointVal(this.x, this.y);
            this.x++;
            if (this.x >= width) {
                this.x = 0;
                this.y++;
            }
            return pt;
        }
    }

    @Override
    public String toString() {
        return "RectVal{" +
               "x=" + x +
               ", y=" + y +
               ", width=" + width +
               ", height=" + height +
               '}';
    }
}
