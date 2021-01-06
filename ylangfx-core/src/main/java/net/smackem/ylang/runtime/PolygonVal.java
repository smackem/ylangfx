package net.smackem.ylang.runtime;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import java.util.*;
import java.util.stream.Collectors;

public class PolygonVal extends GeometryVal<PointVal> {

    private final List<PointVal> vertices;

    private PolygonVal(List<PointVal> vertices, boolean copyVertices) {
        super(ValueType.POLYGON);
        this.vertices = copyVertices ? new ArrayList<>(vertices) : vertices;
    }

    public PolygonVal(List<PointVal> vertices) {
        this(checkVertices(vertices), true);
    }

    /**
     * @return an unmodifiable list of the vertices that define this {@link PolygonVal}.
     */
    public List<? extends PointVal> vertices() {
        return Collections.unmodifiableList(this.vertices);
    }

    @Override
    public GeometryVal<PointVal> translate(PointVal pt) {
        final List<PointVal> vertices = this.vertices.stream()
                .map(vertex -> vertex.translate(pt))
                .collect(Collectors.toList());
        return new PolygonVal(vertices, false);
    }

    @Override
    public RectVal bounds() {
        float left = Float.MAX_VALUE;
        float top = Float.MAX_VALUE;
        float right = Float.MIN_VALUE;
        float bottom = Float.MIN_VALUE;
        for (final PointVal vertex : this.vertices) {
            final float x = vertex.x();
            final float y = vertex.y();
            if (x < left) {
                left = x;
            }
            if (y < top) {
                top = y;
            }
            if (x > right) {
                right = x;
            }
            if (y > bottom) {
                bottom = y;
            }
        }
        return new RectVal(left, top, right - left, bottom - top);
    }

    @Override
    Geometry createGeometry() {
        final GeometryFactory gf = new GeometryFactory();
        final Coordinate[] coordinates = this.vertices.stream()
                .map(vertex -> new Coordinate(vertex.x(), vertex.y()))
                .toArray(Coordinate[]::new);
        return gf.createPolygon(coordinates);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Iterator<PointVal> iterator() {
        final Collection<PointVal> points = collectPoints();
        return points.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PolygonVal values = (PolygonVal) o;
        return Objects.equals(vertices, values.vertices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertices);
    }

    @Override
    public String toString() {
        return "PolygonVal{" +
                "vertices=" + vertices +
                '}';
    }

    private static List<PointVal> checkVertices(List<PointVal> vertices) {
        Objects.requireNonNull(vertices);
        if (vertices.size() < 4) {
            throw new IllegalArgumentException("a polygon must have at least 4 vertices, the first and last of which must be equal");
        }
        if (Objects.equals(vertices.get(0), vertices.get(vertices.size() - 1)) == false) {
            throw new IllegalArgumentException("a polygon must have at least 4 vertices, the first and last of which must be equal");
        }
        return vertices;
    }

    void collectPointsOnHorizLine(float x1, float y, float x2, Collection<PointVal> points) {
        if (x1 > x2) {
            final float temp = x1;
            x1 = x2;
            x2 = temp;
        }
        for (float x = x1; x <= x2; x++) {
            points.add(new PointVal(x, y));
        }
    }

    Collection<PointVal> collectPoints() {
        // buffer to store the x-coordinates of intersections of the polygon with some horizontal line
        final float[] xs = new float[vertices.size()];
        final List<PointVal> points = new ArrayList<>();

        // determine maxima
        final RectVal bounds = bounds();
        final float bottom = bounds.bottom();

        if (bounds.height() == 0) {
            //Special case: polygon only 1 pixel high.
            collectPointsOnHorizLine(bounds.x(), bounds.y(), bounds.right(), points);
            return points;
        }

        // draw, scanning y
        // ----------------
        // the algorithm uses a horizontal line (y) that moves from top to the
        // bottom of the polygon:
        // 1. search intersections with the border lines
        // 2. sort intersections (x_intersect)
        // 3. each two x-coordinates In x_intersect are then inside the polygon
        //    (drawhorzlineclip for a pair of two such points)
        //
        for (float y = bounds.y(); y <= bottom; y++) {
            int intersectionCount = 0;

            for (int i = 0; i < this.vertices.size(); i++) {
                final PointVal pt = this.vertices.get(i);
                int prevI = i - 1;
                if (prevI < 0) {
                    prevI = this.vertices.size() - 1;
                }
                final PointVal prevPt = this.vertices.get(prevI);
                float y1 = prevPt.y(), y2 = pt.y();
                float x1, x2;
                if (y1 < y2) {
                    x1 = prevPt.x();
                    x2 = pt.x();
                } else if (y1 > y2) {
                    y2 = prevPt.y();
                    y1 = pt.y();
                    x2 = prevPt.x();
                    x1 = pt.x();
                } else{ // y1 == y2 : has to be handled as special case (below)
                    continue;
                }
                if (y >= y1 && y < y2 || y == bottom && y2 == bottom) {
                    // Add intersection if y crosses the edge (excluding the lower end), or when we are on the lowest line (maxy)
                    xs[intersectionCount] = (y - y1) * (x2 - x1) / (y2 - y1) + x1;
                    intersectionCount++;
                }
            }
            Arrays.sort(xs, 0, intersectionCount);

            for (int i = 0; i < intersectionCount; i += 2) {
                collectPointsOnHorizLine(xs[i], y, xs[i + 1], points);
            }
        }
        return points;
    }
}
