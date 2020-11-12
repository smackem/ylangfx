package net.smackem.ylang.runtime;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

public class CircleVal extends GeometryVal {

    private final PointVal center;
    private final float radius;

    public CircleVal(PointVal center, float radius) {
        super(ValueType.CIRCLE);
        this.center = Objects.requireNonNull(center);
        this.radius = radius;
    }

    public PointVal center() {
        return this.center;
    }

    public float radius() {
        return this.radius;
    }

    public boolean contains(PointVal pt) {
        return Math.hypot(pt.x() - this.center.x(), pt.y() - this.center.y()) <= this.radius;
    }

    @Override
    protected Geometry createGeometry() {
        final GeometryFactory gf = new GeometryFactory();
        final Point pt = gf.createPoint(new Coordinate(this.center.x(), this.center.y()));
        return pt.buffer(this.radius, 16);
    }

    @Override
    public GeometryVal translate(PointVal pt) {
        return new CircleVal(this.center.translate(pt), this.radius);
    }

    @Override
    public RectVal bounds() {
        final float diameter = radius * 2;
        return new RectVal(
                this.center.x() - radius,
                this.center.y() - radius,
                diameter,
                diameter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final CircleVal values = (CircleVal) o;
        return Float.compare(values.radius, radius) == 0 &&
               center.equals(values.center);
    }

    @Override
    public int hashCode() {
        return Objects.hash(center, radius);
    }

    @Override
    public String toString() {
        return "CircleVal{" +
               "center=" + center +
               ", radius=" + radius +
               '}';
    }

    @Override
    public String toLangString() {
        return String.format(RuntimeParameters.LOCALE,
                "circle(%s, %f)",
                this.center.toLangString(),
                this.radius);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Iterator<Value> iterator() {
        final Collection<Value> points = new ArrayList<>();
        final float x0 = this.center.x();
        final float y0 = this.center.y();
        final int radius = (int) (this.radius + 0.5f);
        float x = radius;
        float y = 0;
        int xChange = 1 - (radius << 1);
        int yChange = 0;
        int radiusError = 0;

        while (x >= y) {
            for (float i = x0 - x; i <= x0 + x; i++) {
                points.add(new PointVal(i, y0 + y));
                points.add(new PointVal(i, y0 - y));
            }
            for (float i = x0 - y; i <= x0 + y; i++) {
                points.add(new PointVal(i, y0 + x));
                points.add(new PointVal(i, y0 - x));
            }

            y++;
            radiusError += yChange;
            yChange += 2;
            if ((radiusError << 1) + xChange > 0) {
                x--;
                radiusError += xChange;
                xChange += 2;
            }
        }
        return points.iterator();
    }
}
