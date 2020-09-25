package net.smackem.ylang.execution.functions;

import net.smackem.ylang.runtime.NumberVal;
import net.smackem.ylang.runtime.RgbVal;
import net.smackem.ylang.runtime.Value;
import net.smackem.ylang.runtime.ValueType;

import java.util.List;

class RgbFunctions {
    private RgbFunctions() {}

    static void register(FunctionRegistry registry) {
        registry.put(FunctionGroup.function("rgb",
                new FunctionOverload(
                        List.of(ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER),
                        RgbFunctions::rgb),
                new FunctionOverload(
                        List.of(ValueType.NUMBER),
                        RgbFunctions::grey)));
        registry.put(FunctionGroup.function("rgb01",
                new FunctionOverload(
                        List.of(ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER),
                        RgbFunctions::rgb01),
                new FunctionOverload(
                        List.of(ValueType.NUMBER),
                        RgbFunctions::grey01)));
        registry.put(FunctionGroup.function("rgba",
                new FunctionOverload(
                        List.of(ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER),
                        RgbFunctions::rgba),
                new FunctionOverload(
                        List.of(ValueType.RGB, ValueType.NUMBER),
                        RgbFunctions::newAlpha)));
        registry.put(FunctionGroup.function("rgba01",
                new FunctionOverload(
                        List.of(ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER),
                        RgbFunctions::rgba01),
                new FunctionOverload(
                        List.of(ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER),
                        RgbFunctions::newAlpha01)));
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
