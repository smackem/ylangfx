package net.smackem.ylang.execution.functions;

import net.smackem.ylang.runtime.*;

import java.util.List;

public class MathFunctions {
    private MathFunctions() {}

    static void register(FunctionRegistry registry) {
        registry.put(new FunctionGroup("sin",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        "returns the sine of the given number in degrees",
                        MathFunctions::sin)));
        registry.put(new FunctionGroup("cos",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        "returns the cosine of the given number in degrees",
                        MathFunctions::cos)));
        registry.put(new FunctionGroup("tan",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        "returns the tangent of the given number in degrees",
                        MathFunctions::tan)));
        registry.put(new FunctionGroup("asin",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        "returns the arc sine in degrees of the given number",
                        MathFunctions::asin)));
        registry.put(new FunctionGroup("acos",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        "returns the arc cosine in degrees of the given number",
                        MathFunctions::acos)));
        registry.put(new FunctionGroup("atan",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        "returns the arc tangent in degrees of the given number",
                        MathFunctions::atan)));
        registry.put(new FunctionGroup("atan2",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER, ValueType.NUMBER),
                        "returns the arc tangent in degrees of `NUMBER-1 / NUMBER-2`",
                        MathFunctions::atan2)));
        registry.put(new FunctionGroup("sqrt",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        "returns the square root of the given number",
                        MathFunctions::sqrt)));
        registry.put(new FunctionGroup("pow",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER, ValueType.NUMBER),
                        "returns `NUMBER-1` to the power of `NUMBER-2`",
                        MathFunctions::pow)));
        registry.put(new FunctionGroup("abs",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        "returns the absolute value of the given NUMBER",
                        MathFunctions::abs)));
        registry.put(new FunctionGroup("round",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        "returns the given NUMBER rounded to the closest integer",
                        MathFunctions::round)));
        registry.put(new FunctionGroup("floor",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        "returns the given NUMBER rounded to the next smallest integer",
                        MathFunctions::floor)));
        registry.put(new FunctionGroup("ceil",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        "returns the given NUMBER rounded to the next largest integer",
                        MathFunctions::ceil)));
        registry.put(new FunctionGroup("trunc",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        "returns the given NUMBER without its fractional part",
                        MathFunctions::floor)));
        registry.put(new FunctionGroup("signum",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        "returns the signum of the given number: -1 for negative numbers, 0 for 0 and 1 for positive numbers",
                        MathFunctions::sign)));
        registry.put(new FunctionGroup("hypot",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER, ValueType.NUMBER),
                        "returns `sqrt(NUMBER-1*NUMBER-1 + NUMBER-2*NUMBER-2)`",
                        MathFunctions::hypot),
                FunctionOverload.function(
                        List.of(ValueType.RGB, ValueType.RGB), """
                            for two RGB values `left` and `right`, returns
                            `rgb(hypot(left.r, right.r), hypot(left.g, right.g), hypot(left.b, right.b))`
                            """,
                        MathFunctions::hypotRgb),
                FunctionOverload.function(
                        List.of(ValueType.IMAGE, ValueType.IMAGE),
                        "combines the two given images by applying the `hypot(RGB, RGB)` function to all pixels",
                        MathFunctions::hypotImage),
                FunctionOverload.function(
                        List.of(ValueType.KERNEL, ValueType.KERNEL),
                        "combines the two given kernels by applying the `hypot(NUMBER, NUMBER)` function to all pixels",
                        MathFunctions::hypotKernel),
                FunctionOverload.function(
                        List.of(ValueType.POINT),
                        "for POINT `p`, returns `hypot(p.x, p.y)`, which is the length of the vector `p`.",
                        MathFunctions::hypotPoint)));
    }

    private static Value sign(List<Value> args) {
        return new NumberVal(Math.signum(((NumberVal) args.get(0)).value()));
    }

    private static Value hypotKernel(List<Value> args) {
        return ((KernelVal) args.get(0)).hypot((KernelVal) args.get(1));
    }

    private static Value hypotImage(List<Value> args) {
        final ImageVal i1 = (ImageVal) args.get(0);
        final ImageVal i2 = (ImageVal) args.get(1);
        return i1.hypot(i2);
    }

    private static Value hypotPoint(List<Value> args) {
        final PointVal pt = (PointVal) args.get(0);
        return new NumberVal((float) Math.hypot(pt.x(), pt.y()));
    }

    private static Value hypotRgb(List<Value> args) {
        final RgbVal a = (RgbVal) args.get(0);
        final RgbVal b = (RgbVal) args.get(1);
        return a.hypot(b);
    }

    private static Value hypot(List<Value> args) {
        return new NumberVal((float) Math.hypot(((NumberVal) args.get(0)).value(), ((NumberVal) args.get(1)).value()));
    }

    private static Value ceil(List<Value> args) {
        return new NumberVal((float) Math.ceil(((NumberVal) args.get(0)).value()));
    }

    private static Value floor(List<Value> args) {
        return new NumberVal((float) Math.floor(((NumberVal) args.get(0)).value()));
    }

    private static Value round(List<Value> args) {
        return new NumberVal((float) Math.round(((NumberVal) args.get(0)).value()));
    }

    private static Value abs(List<Value> args) {
        return new NumberVal(Math.abs(((NumberVal) args.get(0)).value()));
    }

    private static Value pow(List<Value> args) {
        return new NumberVal((float) Math.pow(((NumberVal) args.get(0)).value(), ((NumberVal) args.get(1)).value()));
    }

    private static Value sqrt(List<Value> args) {
        return new NumberVal((float) Math.sqrt(((NumberVal) args.get(0)).value()));
    }

    private static Value atan2(List<Value> args) {
        return new NumberVal((float) Math.toDegrees(Math.atan2(((NumberVal) args.get(0)).value(), ((NumberVal) args.get(1)).value())));
    }

    private static Value atan(List<Value> args) {
        return new NumberVal((float) Math.toDegrees(Math.atan(((NumberVal) args.get(0)).value())));
    }

    private static Value acos(List<Value> args) {
        return new NumberVal((float) Math.toDegrees(Math.acos(((NumberVal) args.get(0)).value())));
    }

    private static Value asin(List<Value> args) {
        return new NumberVal((float) Math.toDegrees(Math.asin(((NumberVal) args.get(0)).value())));
    }

    private static Value tan(List<Value> args) {
        return new NumberVal((float) Math.tan(Math.toRadians(((NumberVal) args.get(0)).value())));
    }

    private static Value cos(List<Value> args) {
        return new NumberVal((float) Math.cos(Math.toRadians(((NumberVal) args.get(0)).value())));
    }

    private static Value sin(List<Value> args) {
        return new NumberVal((float) Math.sin(Math.toRadians(((NumberVal) args.get(0)).value())));
    }
}
