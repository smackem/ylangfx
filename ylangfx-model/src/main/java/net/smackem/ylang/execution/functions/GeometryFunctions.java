package net.smackem.ylang.execution.functions;

import net.smackem.ylang.runtime.*;

import java.util.List;

public class GeometryFunctions {
    private GeometryFunctions() {}

    static void register(FunctionRegistry registry) {
        registry.put(new FunctionGroup("rect",
                FunctionOverload.function(
                        List.of(ValueType.POINT, ValueType.NUMBER, ValueType.NUMBER),
                        GeometryFunctions::rectXYWH),
                FunctionOverload.function(
                        List.of(ValueType.POINT, ValueType.POINT),
                        GeometryFunctions::rectLTRB)));
        registry.put(new FunctionGroup("x",
                FunctionOverload.method(
                        List.of(ValueType.RECT),
                        GeometryFunctions::rectX),
                FunctionOverload.method(
                        List.of(ValueType.POINT),
                        GeometryFunctions::pointX)));
        registry.put(new FunctionGroup("y",
                FunctionOverload.method(
                        List.of(ValueType.RECT),
                        GeometryFunctions::rectY),
                FunctionOverload.method(
                        List.of(ValueType.POINT),
                        GeometryFunctions::pointY)));
        registry.put(new FunctionGroup("width",
                FunctionOverload.method(
                        List.of(ValueType.RECT),
                        GeometryFunctions::rectWidth)));
        registry.put(new FunctionGroup("height",
                FunctionOverload.method(
                        List.of(ValueType.RECT),
                        GeometryFunctions::rectHeight)));
        registry.put(new FunctionGroup("right",
                FunctionOverload.method(
                        List.of(ValueType.RECT),
                        GeometryFunctions::rectRight)));
        registry.put(new FunctionGroup("bottom",
                FunctionOverload.method(
                        List.of(ValueType.RECT),
                        GeometryFunctions::rectBottom)));
        registry.put(new FunctionGroup("inflate",
                FunctionOverload.method(
                        List.of(ValueType.RECT, ValueType.NUMBER, ValueType.NUMBER),
                        GeometryFunctions::rectInflate)));
        registry.put(new FunctionGroup("line",
                FunctionOverload.function(
                        List.of(ValueType.POINT, ValueType.POINT),
                        GeometryFunctions::line)));
        registry.put(new FunctionGroup("p1",
                FunctionOverload.method(
                        List.of(ValueType.LINE),
                        GeometryFunctions::linePoint1)));
        registry.put(new FunctionGroup("p2",
                FunctionOverload.method(
                        List.of(ValueType.LINE),
                        GeometryFunctions::linePoint2)));
        registry.put(new FunctionGroup("length",
                FunctionOverload.method(
                        List.of(ValueType.LINE),
                        GeometryFunctions::lineLength)));
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

    private static Value rectHeight(List<Value> args) {
        final RectVal rect = (RectVal) args.get(0);
        return new NumberVal(rect.height());
    }

    private static Value rectWidth(List<Value> args) {
        final RectVal rect = (RectVal) args.get(0);
        return new NumberVal(rect.width());
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
