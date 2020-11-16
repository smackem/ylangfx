package net.smackem.ylang.runtime;

import org.locationtech.jts.geom.Geometry;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class GeometryVal<T extends Value> extends Value implements Iterable<T> {

    private Geometry geometry;

    GeometryVal(ValueType type) {
        super(type);
    }

    public abstract GeometryVal<T> translate(PointVal pt);

    public abstract RectVal bounds();

    public final Geometry geometry() {
        if (this.geometry == null) {
            this.geometry = createGeometry();
        }
        return this.geometry;
    }

    abstract Geometry createGeometry();

    public static <T extends Value> List<PointVal> intersect(GeometryVal<T> g1, GeometryVal<T> g2) {
        Objects.requireNonNull(g1);
        Objects.requireNonNull(g2);
        final Geometry intersection = g1.geometry().intersection(g2.geometry());
        return Arrays.stream(intersection.getCoordinates())
                .map(coordinate -> new PointVal((float) coordinate.x, (float) coordinate.y))
                .collect(Collectors.toList());
    }

    public static <T extends Value> float distance(GeometryVal<T> g1, GeometryVal<T> g2) {
        Objects.requireNonNull(g1);
        Objects.requireNonNull(g2);
        return (float) g1.geometry().distance(g2.geometry);
    }
}
