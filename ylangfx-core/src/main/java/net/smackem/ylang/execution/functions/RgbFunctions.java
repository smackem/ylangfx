package net.smackem.ylang.execution.functions;

import net.smackem.ylang.runtime.*;

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
                        RgbFunctions::grey),
                FunctionOverload.method(
                        List.of(ValueType.HSV),
                        RgbFunctions::rgbFromHsv)));
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
                        RgbFunctions::withAlpha),
                FunctionOverload.function(
                        List.of(ValueType.HSV, ValueType.NUMBER),
                        RgbFunctions::rgbFromHsvWithAlpha)));
        registry.put(new FunctionGroup("rgba01",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER),
                        RgbFunctions::rgba01),
                FunctionOverload.function(
                        List.of(ValueType.RGB, ValueType.NUMBER),
                        RgbFunctions::withAlpha01),
                FunctionOverload.function(
                        List.of(ValueType.HSV, ValueType.NUMBER),
                        RgbFunctions::rgbFromHsvWithAlpha01)));
        // rgb methods
        registry.put(new FunctionGroup("r",
                FunctionOverload.method(List.of(ValueType.RGB), RgbFunctions::red),
                FunctionOverload.method(List.of(ValueType.RGB, ValueType.NUMBER), RgbFunctions::withRed)));
        registry.put(new FunctionGroup("r01",
                FunctionOverload.method(List.of(ValueType.RGB), RgbFunctions::red01)));
        registry.put(new FunctionGroup("g",
                FunctionOverload.method(List.of(ValueType.RGB), RgbFunctions::green),
                FunctionOverload.method(List.of(ValueType.RGB, ValueType.NUMBER), RgbFunctions::withGreen)));
        registry.put(new FunctionGroup("g01",
                FunctionOverload.method(List.of(ValueType.RGB), RgbFunctions::green01)));
        registry.put(new FunctionGroup("b",
                FunctionOverload.method(List.of(ValueType.RGB), RgbFunctions::blue),
                FunctionOverload.method(List.of(ValueType.RGB, ValueType.NUMBER), RgbFunctions::withBlue)));
        registry.put(new FunctionGroup("b01",
                FunctionOverload.method(List.of(ValueType.RGB), RgbFunctions::blue01)));
        registry.put(new FunctionGroup("a",
                FunctionOverload.method(List.of(ValueType.RGB), RgbFunctions::alpha),
                FunctionOverload.method(List.of(ValueType.RGB, ValueType.NUMBER), RgbFunctions::withAlpha)));
        registry.put(new FunctionGroup("a01",
                FunctionOverload.method(List.of(ValueType.RGB), RgbFunctions::alpha01)));
        registry.put(new FunctionGroup("intensity",
                FunctionOverload.method(List.of(ValueType.RGB), RgbFunctions::intensity)));
        registry.put(new FunctionGroup("i",
                FunctionOverload.method(List.of(ValueType.RGB), RgbFunctions::intensity)));
        registry.put(new FunctionGroup("intensity01",
                FunctionOverload.method(List.of(ValueType.RGB), RgbFunctions::intensity01)));
        registry.put(new FunctionGroup("i01",
                FunctionOverload.method(List.of(ValueType.RGB), RgbFunctions::intensity01)));
        registry.put(new FunctionGroup("over",
                FunctionOverload.method(
                        List.of(ValueType.RGB, ValueType.RGB),
                        RgbFunctions::rgbOverRgb),
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.IMAGE),
                        RgbFunctions::imageOverImage)));
        registry.put(new FunctionGroup("grey",
                FunctionOverload.method(List.of(ValueType.RGB), RgbFunctions::greyscale)));
        registry.put(new FunctionGroup("hsv",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER),
                        RgbFunctions::hsv),
                FunctionOverload.method(
                        List.of(ValueType.RGB),
                        RgbFunctions::hsvFromRgb)));
        registry.put(new FunctionGroup("h",
                FunctionOverload.method(List.of(ValueType.HSV), RgbFunctions::hue),
                FunctionOverload.method(List.of(ValueType.HSV, ValueType.NUMBER), RgbFunctions::withHue)));
        registry.put(new FunctionGroup("s",
                FunctionOverload.method(List.of(ValueType.HSV), RgbFunctions::saturation),
                FunctionOverload.method(List.of(ValueType.HSV, ValueType.NUMBER), RgbFunctions::withSaturation)));
        registry.put(new FunctionGroup("v",
                FunctionOverload.method(List.of(ValueType.HSV), RgbFunctions::value),
                FunctionOverload.method(List.of(ValueType.HSV, ValueType.NUMBER), RgbFunctions::withValue)));
        registry.put(new FunctionGroup("add_hue",
                FunctionOverload.method(List.of(ValueType.HSV, ValueType.NUMBER), RgbFunctions::addHue)));
        registry.put(new FunctionGroup("add_saturation",
                FunctionOverload.method(List.of(ValueType.HSV, ValueType.NUMBER), RgbFunctions::addSaturation)));
        registry.put(new FunctionGroup("add_value",
                FunctionOverload.method(List.of(ValueType.HSV, ValueType.NUMBER), RgbFunctions::addValue)));
    }

    private static Value imageOverImage(List<Value> args) {
        ImageVal i1 = (ImageVal) args.get(0);
        ImageVal i2 = (ImageVal) args.get(1);
        return i1.over(i2);
    }

    private static Value greyscale(List<Value> args) {
        final RgbVal rgb = ((RgbVal) args.get(0));
        final float i = rgb.intensity();
        return new RgbVal(i, i, i, rgb.a());
    }

    private static Value withHue(List<Value> args) {
        final HsvVal hsv = (HsvVal) args.get(0);
        return new HsvVal(((NumberVal) args.get(1)).value(), hsv.saturation(), hsv.value());
    }

    private static Value withSaturation(List<Value> args) {
        final HsvVal hsv = (HsvVal) args.get(0);
        return new HsvVal(hsv.hue(), ((NumberVal) args.get(1)).value(), hsv.value());
    }

    private static Value withValue(List<Value> args) {
        final HsvVal hsv = (HsvVal) args.get(0);
        return new HsvVal(hsv.hue(), hsv.saturation(), ((NumberVal) args.get(1)).value());
    }

    private static Value addHue(List<Value> args) {
        final HsvVal hsv = (HsvVal) args.get(0);
        return new HsvVal((hsv.hue() + ((NumberVal) args.get(1)).value()) % 360, hsv.saturation(), hsv.value());
    }

    private static Value addSaturation(List<Value> args) {
        final HsvVal hsv = (HsvVal) args.get(0);
        return new HsvVal(hsv.hue(), hsv.saturation() + ((NumberVal) args.get(1)).value(), hsv.value());
    }

    private static Value addValue(List<Value> args) {
        final HsvVal hsv = (HsvVal) args.get(0);
        return new HsvVal(hsv.hue(), hsv.saturation(), hsv.value() + ((NumberVal) args.get(1)).value());
    }

    private static Value withRed(List<Value> args) {
        final RgbVal rgb = ((RgbVal) args.get(0));
        return new RgbVal(((NumberVal) args.get(1)).value(), rgb.g(), rgb.b(), rgb.a());
    }

    private static Value withGreen(List<Value> args) {
        final RgbVal rgb = ((RgbVal) args.get(0));
        return new RgbVal(rgb.r(), ((NumberVal) args.get(1)).value(), rgb.b(), rgb.a());
    }

    private static Value withBlue(List<Value> args) {
        final RgbVal rgb = ((RgbVal) args.get(0));
        return new RgbVal(rgb.r(), rgb.g(), ((NumberVal) args.get(1)).value(), rgb.a());
    }

    private static Value rgbFromHsvWithAlpha01(List<Value> args) {
        final RgbVal rgb = ((HsvVal) args.get(0)).toRgb();
        return new RgbVal(rgb.r(), rgb.g(), rgb.b(), ((NumberVal) args.get(1)).value() * 255);
    }

    private static Value rgbFromHsvWithAlpha(List<Value> args) {
        final RgbVal rgb = ((HsvVal) args.get(0)).toRgb();
        return new RgbVal(rgb.r(), rgb.g(), rgb.b(), ((NumberVal) args.get(1)).value());
    }

    private static Value hue(List<Value> args) {
        return new NumberVal(((HsvVal) args.get(0)).hue());
    }

    private static Value saturation(List<Value> args) {
        return new NumberVal(((HsvVal) args.get(0)).saturation());
    }

    private static Value value(List<Value> args) {
        return new NumberVal(((HsvVal) args.get(0)).value());
    }

    private static Value hsvFromRgb(List<Value> args) {
        return HsvVal.fromRgb((RgbVal) args.get(0));
    }

    private static Value hsv(List<Value> args) {
        return new HsvVal(((NumberVal) args.get(0)).value(), ((NumberVal) args.get(1)).value(), ((NumberVal) args.get(2)).value());
    }

    private static Value rgbFromHsv(List<Value> args) {
        return ((HsvVal) args.get(0)).toRgb();
    }

    private static Value rgbOverRgb(List<Value> args) {
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

    private static Value withAlpha01(List<Value> args) {
        final RgbVal rgb = (RgbVal) args.get(0);
        return new RgbVal(
                rgb.r(),
                rgb.g(),
                rgb.b(),
                ((NumberVal) args.get(1)).value() * 255f);
    }

    private static Value withAlpha(List<Value> args) {
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
