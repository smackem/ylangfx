package net.smackem.ylang.execution.functions;

import net.smackem.ylang.runtime.NumberVal;
import net.smackem.ylang.runtime.RgbVal;
import net.smackem.ylang.runtime.Value;
import net.smackem.ylang.runtime.ValueType;

import java.util.List;

class RgbFunctions {
    private RgbFunctions() {}

    static void register(FunctionRegistry registry) {
        registry.put(new FunctionGroup("rgb",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER),
                        RgbFunctions::rgb),
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        RgbFunctions::grey)));
        registry.put(new FunctionGroup("rgb01",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER),
                        RgbFunctions::rgb01),
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        RgbFunctions::grey01)));
        registry.put(new FunctionGroup("rgba",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER),
                        RgbFunctions::rgba),
                FunctionOverload.function(
                        List.of(ValueType.RGB, ValueType.NUMBER),
                        RgbFunctions::newAlpha)));
        registry.put(new FunctionGroup("rgba01",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER),
                        RgbFunctions::rgba01),
                FunctionOverload.function(
                        List.of(ValueType.RGB, ValueType.NUMBER),
                        RgbFunctions::newAlpha01)));
        // rgb methods
        registry.put(new FunctionGroup("r",
                FunctionOverload.method(List.of(ValueType.RGB), RgbFunctions::red)));
        registry.put(new FunctionGroup("r01",
                FunctionOverload.method(List.of(ValueType.RGB), RgbFunctions::red01)));
        registry.put(new FunctionGroup("g",
                FunctionOverload.method(List.of(ValueType.RGB), RgbFunctions::green)));
        registry.put(new FunctionGroup("g01",
                FunctionOverload.method(List.of(ValueType.RGB), RgbFunctions::green01)));
        registry.put(new FunctionGroup("b",
                FunctionOverload.method(List.of(ValueType.RGB), RgbFunctions::blue)));
        registry.put(new FunctionGroup("b01",
                FunctionOverload.method(List.of(ValueType.RGB), RgbFunctions::blue01)));
        registry.put(new FunctionGroup("a",
                FunctionOverload.method(List.of(ValueType.RGB), RgbFunctions::alpha)));
        registry.put(new FunctionGroup("a01",
                FunctionOverload.method(List.of(ValueType.RGB), RgbFunctions::alpha01)));
        registry.put(new FunctionGroup("intensity",
                FunctionOverload.method(List.of(ValueType.RGB), RgbFunctions::intensity)));
        registry.put(new FunctionGroup("intensity01",
                FunctionOverload.method(List.of(ValueType.RGB), RgbFunctions::intensity01)));
        registry.put(new FunctionGroup("over",
                FunctionOverload.method(List.of(ValueType.RGB, ValueType.RGB),
                        RgbFunctions::over)));
    }

    private static Value over(List<Value> args) {
        return ((RgbVal) args.get(0)).over((RgbVal) args.get(1));
    }

    private static Value red(List<Value> args) {
        return new NumberVal(((RgbVal) args.get(0)).r());
    }

    private static Value red01(List<Value> args) {
        return new NumberVal(((RgbVal) args.get(0)).r01());
    }

    private static Value green(List<Value> args) {
        return new NumberVal(((RgbVal) args.get(0)).g());
    }

    private static Value green01(List<Value> args) {
        return new NumberVal(((RgbVal) args.get(0)).g01());
    }

    private static Value blue(List<Value> args) {
        return new NumberVal(((RgbVal) args.get(0)).b());
    }

    private static Value blue01(List<Value> args) {
        return new NumberVal(((RgbVal) args.get(0)).b01());
    }

    private static Value alpha(List<Value> args) {
        return new NumberVal(((RgbVal) args.get(0)).a());
    }

    private static Value alpha01(List<Value> args) {
        return new NumberVal(((RgbVal) args.get(0)).a01());
    }

    private static Value intensity(List<Value> args) {
        return new NumberVal(((RgbVal) args.get(0)).intensity());
    }

    private static Value intensity01(List<Value> args) {
        return new NumberVal(((RgbVal) args.get(0)).intensity01());
    }

    private static Value newAlpha01(List<Value> args) {
        final RgbVal rgb = (RgbVal) args.get(0);
        return new RgbVal(
                rgb.r(),
                rgb.g(),
                rgb.b(),
                ((NumberVal) args.get(1)).value() * 255f);
    }

    private static Value newAlpha(List<Value> args) {
        final RgbVal rgb = (RgbVal) args.get(0);
        return new RgbVal(
                rgb.r(),
                rgb.g(),
                rgb.b(),
                ((NumberVal) args.get(1)).value());
    }

    private static Value rgba01(List<Value> args) {
        return new RgbVal(
                ((NumberVal) args.get(0)).value() * 255f,
                ((NumberVal) args.get(1)).value() * 255f,
                ((NumberVal) args.get(2)).value() * 255f,
                ((NumberVal) args.get(3)).value() * 255f);
    }

    private static Value grey01(List<Value> args) {
        final float intensity = ((NumberVal)args.get(0)).value() * 255f;
        return new RgbVal(intensity, intensity, intensity, 255f);
    }

    private static Value rgb01(List<Value> args) {
        return new RgbVal(
                ((NumberVal) args.get(0)).value() * 255f,
                ((NumberVal) args.get(1)).value() * 255f,
                ((NumberVal) args.get(2)).value() * 255f,
                255f);
    }

    private static Value rgb(List<Value> args) {
        return new RgbVal(
                ((NumberVal) args.get(0)).value(),
                ((NumberVal) args.get(1)).value(),
                ((NumberVal) args.get(2)).value(),
                255f);
    }

    private static Value grey(List<Value> args) {
        final float intensity = ((NumberVal)args.get(0)).value();
        return new RgbVal(intensity, intensity, intensity, 255f);
    }

    private static Value rgba(List<Value> args) {
        return new RgbVal(
                ((NumberVal) args.get(0)).value(),
                ((NumberVal) args.get(1)).value(),
                ((NumberVal) args.get(2)).value(),
                ((NumberVal) args.get(3)).value());
    }
}
