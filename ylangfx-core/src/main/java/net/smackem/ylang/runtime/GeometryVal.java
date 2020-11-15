package net.smackem.ylang.runtime;

import org.locationtech.jts.geom.Geometry;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class GeometryVal extends Value implements Iterable<Value> {

    private Geometry geometry;

    GeometryVal(ValueType type) {
        super(type);
    }

    public abstract GeometryVal translate(PointVal pt);

    public abstract RectVal bounds();

    public final Geometry geometry() {
        if (this.geometry == null) {
            this.geometry = createGeometry();
        }
        return this.geometry;
    }

    abstract Geometry createGeometry();

    public static List<PointVal> intersect(GeometryVal g1, GeometryVal g2) {
        Objects.requireNonNull(g1);
        Objects.requireNonNull(g2);
        final Geometry intersection = g1.geometry().intersection(g2.geometry());
        return Arrays.stream(intersection.getCoordinates())
                .map(coordinate -> new PointVal((float) coordinate.x, (float) coordinate.y))
                .collect(Collectors.toList());
    }

    public static float distance(GeometryVal g1, GeometryVal g2) {
        Objects.requireNonNull(g1);
        Objects.requireNonNull(g2);
        return (float) g1.geometry().distance(g2.geometry);
    }
}
