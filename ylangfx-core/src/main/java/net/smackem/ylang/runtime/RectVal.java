package net.smackem.ylang.runtime;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import java.util.Iterator;
import java.util.Objects;

public class RectVal extends GeometryVal<PointVal> {
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
        return pt.x() >= this.x && pt.x() <= this.right() &&
                pt.y() >= this.y && pt.y() <= this.bottom();
    }

    public boolean contains(RectVal rc) {
        return rc.x >= this.x && rc.right() <= this.right() &&
               rc.y >= this.y && rc.bottom() <= this.bottom();
    }

    @Override
    Geometry createGeometry() {
        final GeometryFactory gf = new GeometryFactory();
        return gf.createLinearRing(new Coordinate[] {
                new Coordinate(this.x, this.y),
                new Coordinate(right(), this.y),
                new Coordinate(right(), bottom()),
                new Coordinate(this.x, bottom()),
                new Coordinate(this.x, this.y),
        });
    }

    @Override
    public GeometryVal<PointVal> translate(PointVal pt) {
        return new RectVal(this.x + pt.x(), this.y + pt.y(), this.width, this.height);
    }

    @Override
    public RectVal bounds() {
        return this;
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
    public Iterator<PointVal> iterator() {
        return new PointIterator(this);
    }

    @Override
    public String toLangString() {
        return String.format(RuntimeParameters.LOCALE, "rect(%f;%f, %f, %f)", this.x, this.y, this.width, this.height);
    }

    private static class PointIterator implements Iterator<PointVal> {
        final int left, right, top, bottom;
        int x, y;

        PointIterator(RectVal rect) {
            this.left = (int) rect.x;
            this.top = (int) rect.y;
            this.right = (int) rect.right();
            this.bottom = (int) rect.bottom();
            this.x = this.left;
            this.y = this.top;
        }

        @Override
        public boolean hasNext() {
            return this.y < this.bottom;
        }

        @Override
        public PointVal next() {
            final var pt = new PointVal(this.x, this.y);
            this.x++;
            if (this.x >= this.right) {
                this.x = this.left;
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
