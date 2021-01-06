package net.smackem.ylang.execution.functions;

import net.smackem.ylang.runtime.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class GeometryFunctions {
    private GeometryFunctions() {}

    @SuppressWarnings("DuplicatedCode")
    static void register(FunctionRegistry registry) {
        registry.put(new FunctionGroup("rect",
                FunctionOverload.function(
                        List.of(ValueType.POINT, ValueType.NUMBER, ValueType.NUMBER),
                        "creates a RECT with the given POINT as upper-left corner and the given width and height",
                        GeometryFunctions::rectXYWH),
                FunctionOverload.function(
                        List.of(ValueType.POINT, ValueType.POINT),"""
                            creates a RECT from points `a` and `b`,
                            where `a` is the upper-left corner and `b` the bottom-right corner.
                            """,
                        GeometryFunctions::rectLTRB)));
        registry.put(new FunctionGroup("x",
                FunctionOverload.method(
                        List.of(ValueType.RECT),
                        "returns the x-coordinate of the RECT's upper-left corner.",
                        GeometryFunctions::rectX),
                FunctionOverload.method(
                        List.of(ValueType.POINT),
                        "returns the x-coordinate of the POINT.",
                        GeometryFunctions::pointX)));
        registry.put(new FunctionGroup("y",
                FunctionOverload.method(
                        List.of(ValueType.RECT),
                        "returns the y-coordinate of the RECT's upper-left corner.",
                        GeometryFunctions::rectY),
                FunctionOverload.method(
                        List.of(ValueType.POINT),
                        "returns the y-coordinate of the POINT.",
                        GeometryFunctions::pointY)));
        registry.put(new FunctionGroup("right",
                FunctionOverload.method(
                        List.of(ValueType.RECT),
                        "returns the x-coordinate of the RECT's bottom-right corner.",
                        GeometryFunctions::rectRight)));
        registry.put(new FunctionGroup("bottom",
                FunctionOverload.method(
                        List.of(ValueType.RECT),
                        "returns the y-coordinate of the RECT's bottom-right corner.",
                        GeometryFunctions::rectBottom)));
        registry.put(new FunctionGroup("inflate",
                FunctionOverload.method(
                        List.of(ValueType.RECT, ValueType.NUMBER, ValueType.NUMBER), """
                            returns given RECT, inflated by NUMBER `a` in left and right direction,
                            and inflated by NUMBER `b` in upward and downward.
                            """,
                        GeometryFunctions::rectInflate)));
        // rect width and height are defined in CommonFunctions
        registry.put(new FunctionGroup("line",
                FunctionOverload.function(
                        List.of(ValueType.POINT, ValueType.POINT),
                        "creates a LINE from POINT `a` to POINT `b`",
                        GeometryFunctions::line)));
        registry.put(new FunctionGroup("p1",
                FunctionOverload.method(
                        List.of(ValueType.LINE),
                        "returns the start POINT of the given LINE.",
                        GeometryFunctions::linePoint1)));
        registry.put(new FunctionGroup("p2",
                FunctionOverload.method(
                        List.of(ValueType.LINE),
                        "returns the end POINT of the given LINE.",
                        GeometryFunctions::linePoint2)));
        registry.put(new FunctionGroup("length",
                FunctionOverload.method(
                        List.of(ValueType.LINE),
                        "returns the length of the given LINE.",
                        GeometryFunctions::lineLength)));
        registry.put(new FunctionGroup("circle",
                FunctionOverload.function(
                        List.of(ValueType.POINT, ValueType.NUMBER),
                        "creates a new CIRCLE with the given center POINT and radius.",
                        GeometryFunctions::circle)));
        registry.put(new FunctionGroup("center",
                FunctionOverload.method(
                        List.of(ValueType.CIRCLE),
                        "returns the center POINT of the given CIRCLE.",
                        GeometryFunctions::circleCenter)));
        registry.put(new FunctionGroup("radius",
                FunctionOverload.method(
                        List.of(ValueType.CIRCLE),
                        "returns the radius of the given CIRCLE.",
                        GeometryFunctions::circleRadius)));
        registry.put(new FunctionGroup("polygon",
                FunctionOverload.function(
                        List.of(ValueType.LIST), """
                            creates a new POLYGON with the vertices in the given list.
                            the list must contain at least 4 POINTs, the first and last of which must be equal.
                            """,
                        GeometryFunctions::polygon)));
        registry.put(new FunctionGroup("vertices",
                FunctionOverload.method(
                        List.of(ValueType.POLYGON),
                        "returns a LIST containing the POINTs that define this POLYGON.",
                        GeometryFunctions::polygonVertices)));
        final Collection<ValueType> geometryTypes = ValueType.publicValues().stream()
                .filter(ValueType::isGeometry)
                .collect(Collectors.toList());
        final int typeCount = geometryTypes.size();
        final List<FunctionOverload> intersectOverloads = new ArrayList<>(typeCount);
        final List<FunctionOverload> distanceOverloads = new ArrayList<>(typeCount);
        final List<FunctionOverload> translateOverloads = new ArrayList<>(typeCount);
        final List<FunctionOverload> boundsOverloads = new ArrayList<>(typeCount);
        for (final ValueType left : geometryTypes) {
            for (final ValueType right : geometryTypes) {
                intersectOverloads.add(FunctionOverload.method(List.of(left, right),
                        "intersects this geometry with another geometry and returns a LIST of the intersection POINTs.",
                        GeometryFunctions::intersect));
                distanceOverloads.add(FunctionOverload.method(List.of(left, right),
                        "returns the distance between this geometry and another geometry.",
                        GeometryFunctions::distance));
            }
            translateOverloads.add(FunctionOverload.method(List.of(left, ValueType.POINT),
                    "returns a copy of this geometry, translated by the given POINT.",
                    GeometryFunctions::translateByPoint));
            translateOverloads.add(FunctionOverload.method(List.of(left, ValueType.NUMBER, ValueType.NUMBER),
                    "returns a copy of this geometry, translated by the given x- and y-offsets.",
                    GeometryFunctions::translateByXY));
            boundsOverloads.add(FunctionOverload.method(List.of(left),
                    "returns the bounding RECT of the given geometry.",
                    GeometryFunctions::bounds));
        }
        distanceOverloads.add(FunctionOverload.method(List.of(ValueType.RGB, ValueType.RGB),
                "returns the distance between this RGB and the other RGB as in sqrt(dR*dR + dG*dG + dB*dB)",
                GeometryFunctions::distanceRgb));
        registry.put(new FunctionGroup("intersect", intersectOverloads));
        registry.put(new FunctionGroup("distance", distanceOverloads));
        registry.put(new FunctionGroup("translate", translateOverloads));
        boundsOverloads.add(FunctionOverload.method(
                List.of(ValueType.IMAGE),
                "returns the bounds of the given IMAGE. the upper-left corner is always 0;0",
                GeometryFunctions::matrixBounds));
        boundsOverloads.add(FunctionOverload.method(
                List.of(ValueType.KERNEL),
                "returns the bounds of the given KERNEL. the upper-left corner is always 0;0",
                GeometryFunctions::matrixBounds));
        registry.put(new FunctionGroup("bounds", boundsOverloads));
    }

    private static Value polygonVertices(List<Value> args) {
        final PolygonVal polygon = (PolygonVal) args.get(0);
        return new ListVal(polygon.vertices());
    }

    private static Value matrixBounds(List<Value> args) {
        final MatrixVal<?> matrix = (MatrixVal<?>) args.get(0);
        return new RectVal(0, 0, matrix.width(), matrix.height());
    }

    private static Value bounds(List<Value> values) {
        return ((GeometryVal<?>) values.get(0)).bounds();
    }

    private static Value distanceRgb(List<Value> args) {
        return new NumberVal(((RgbVal) args.get(0)).distance((RgbVal) args.get(1)));
    }

    private static Value translateByPoint(List<Value> args) {
        //noinspection unchecked
        return ((GeometryVal<PointVal>) args.get(0)).translate((PointVal) args.get(1));
    }

    private static Value translateByXY(List<Value> args) {
        final float x = ((NumberVal) args.get(1)).value();
        final float y = ((NumberVal) args.get(2)).value();
        //noinspection unchecked
        return ((GeometryVal<PointVal>) args.get(0)).translate(new PointVal(x, y));
    }

    private static Value polygon(List<Value> args) {
        final ListVal list = (ListVal) args.get(0);
        final List<PointVal> vertices = new ArrayList<>();
        for (final Value v : list) {
            if (v instanceof PointVal == false) {
                throw new IllegalArgumentException("polygon vertices must be of type POINT");
            }
            vertices.add((PointVal) v);
        }
        return new PolygonVal(vertices);
    }

    @SuppressWarnings("unchecked")
    private static Value distance(List<Value> args) {
        final GeometryVal<PointVal> lg = (GeometryVal<PointVal>) args.get(0);
        final GeometryVal<PointVal> rg = (GeometryVal<PointVal>) args.get(1);
        return new NumberVal(GeometryVal.distance(lg, rg));
    }

    @SuppressWarnings("unchecked")
    private static Value intersect(List<Value> args) {
        final GeometryVal<PointVal> lg = (GeometryVal<PointVal>) args.get(0);
        final GeometryVal<PointVal> rg = (GeometryVal<PointVal>) args.get(1);
        return new ListVal(GeometryVal.intersect(lg, rg));
    }

    private static Value circleRadius(List<Value> args) {
        return new NumberVal(((CircleVal) args.get(0)).radius());
    }

    private static Value circleCenter(List<Value> args) {
        return ((CircleVal) args.get(0)).center();
    }

    private static Value circle(List<Value> args) {
        return new CircleVal((PointVal) args.get(0), ((NumberVal) args.get(1)).value());
    }

    private static Value rectInflate(List<Value> args) {
        final RectVal rect = (RectVal) args.get(0);
        return rect.inflate(((NumberVal) args.get(1)).value(), ((NumberVal) args.get(2)).value());
    }

    private static Value lineLength(List<Value> args) {
        final LineVal line = ((LineVal) args.get(0));
        return new NumberVal(line.length());
    }

    private static Value linePoint1(List<Value> args) {
        final LineVal line = ((LineVal) args.get(0));
        return new PointVal(line.x1(), line.y1());
    }

    private static Value linePoint2(List<Value> args) {
        final LineVal line = ((LineVal) args.get(0));
        return new PointVal(line.x2(), line.y2());
    }

    private static Value line(List<Value> args) {
        final PointVal p1 = (PointVal) args.get(0);
        final PointVal p2 = (PointVal) args.get(1);
        return new LineVal(p1.x(), p1.y(), p2.x(), p2.y());
    }

    private static Value rectBottom(List<Value> args) {
        final RectVal rect = (RectVal) args.get(0);
        return new NumberVal(rect.bottom());
    }

    private static Value rectRight(List<Value> args) {
        final RectVal rect = (RectVal) args.get(0);
        return new NumberVal(rect.right());
    }

    private static Value pointY(List<Value> args) {
        final PointVal pt = (PointVal) args.get(0);
        return new NumberVal(pt.y());
    }

    private static Value rectY(List<Value> args) {
        final RectVal rect = (RectVal) args.get(0);
        return new NumberVal(rect.y());
    }

    private static Value pointX(List<Value> args) {
        final PointVal pt = (PointVal) args.get(0);
        return new NumberVal(pt.x());
    }

    private static Value rectX(List<Value> args) {
        final RectVal rect = (RectVal) args.get(0);
        return new NumberVal(rect.x());
    }

    private static Value rectLTRB(List<Value> args) {
        final PointVal leftTop = (PointVal) args.get(0);
        final PointVal rightBottom = (PointVal) args.get(1);
        final float x = leftTop.x();
        final float y = leftTop.y();
        return new RectVal(x, y, rightBottom.x() - x, rightBottom.y() - y);
    }

    private static Value rectXYWH(List<Value> args) {
        final PointVal leftTop = (PointVal) args.get(0);
        return new RectVal(leftTop.x(), leftTop.y(),
                ((NumberVal) args.get(1)).value(),
                ((NumberVal) args.get(2)).value());
    }
}
