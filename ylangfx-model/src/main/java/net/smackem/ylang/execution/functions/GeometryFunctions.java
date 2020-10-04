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
