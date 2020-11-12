package net.smackem.ylang.runtime;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import java.util.Iterator;
import java.util.Objects;

public final class PointVal extends GeometryVal {
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

    @Override
    public PointVal translate(PointVal other) {
        return new PointVal(this.x + other.x, this.y + other.y);
    }

    @Override
    public RectVal bounds() {
        return new RectVal(this.x, this.y, this.x, this.y);
    }

    @Override
    Geometry createGeometry() {
        final GeometryFactory gf = new GeometryFactory();
        return gf.createPoint(new Coordinate(this.x, this.y));
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

    @Override
    public String toString() {
        return "PointVal{" +
               "x=" + x +
               ", y=" + y +
               '}';
    }

    @Override
    public String toLangString() {
        return String.format(RuntimeParameters.LOCALE, "%f;%f", this.x, this.y);
    }

    @Override
    public Iterator<Value> iterator() {
        return new Iterator<>() {
            private boolean hasReturned;

            @Override
            public boolean hasNext() {
                return this.hasReturned == false;
            }

            @Override
            public Value next() {
                this.hasReturned = true;
                return PointVal.this;
            }
        };
    }
}
