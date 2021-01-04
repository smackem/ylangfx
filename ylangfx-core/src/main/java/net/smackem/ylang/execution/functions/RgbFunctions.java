package net.smackem.ylang.execution.functions;

import net.smackem.ylang.runtime.*;

import java.util.List;

class RgbFunctions {
    private RgbFunctions() {}

    static void register(FunctionRegistry registry) {
        registry.put(new FunctionGroup("rgb",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER),
                        "creates an RGB value with the given channel values (r, g, and b) and an alpha value of 255",
                        RgbFunctions::rgb),
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        "creates a grey RGB value with all three color channels set to the given number and alpha 255",
                        RgbFunctions::grey),
                FunctionOverload.method(
                        List.of(ValueType.HSV),
                        "converts this HSV value into RGB format and returns the newly created RGB value",
                        RgbFunctions::rgbFromHsv)));
        registry.put(new FunctionGroup("rgb01",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER),
                        "creates an RGB value from the given channel values (r, g, and b) in the range `0..1` and an alpha value of 255",
                        RgbFunctions::rgb01),
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        "creates a grey RGB value from the given number in the range `0..1` and alpha 255",
                        RgbFunctions::grey01)));
        registry.put(new FunctionGroup("rgba",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER),
                        "creates an RGB value with the given channel values (r, g, b and a)",
                        RgbFunctions::rgba),
                FunctionOverload.method(
                        List.of(ValueType.HSV, ValueType.NUMBER),
                        "converts this HSV value into RGB format, sets alpha to the given NUMBER and returns the newly created RGB value",
                        RgbFunctions::rgbFromHsvWithAlpha)));
        registry.put(new FunctionGroup("rgba01",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER),
                        "creates an RGB value from the given channel values (r, g, b and a) in the range `0..1`",
                        RgbFunctions::rgba01),
                FunctionOverload.method(
                        List.of(ValueType.HSV, ValueType.NUMBER),
                        "converts this HSV value into RGB format, sets alpha to the given NUMBER * 255 and returns the newly created RGB value",
                        RgbFunctions::rgbFromHsvWithAlpha01)));
        // rgb methods
        registry.put(new FunctionGroup("r",
                FunctionOverload.method(List.of(ValueType.RGB),
                        "returns the value of the red color channel. the normalized range is 0..255, but all other values are possible.",
                        RgbFunctions::red),
                FunctionOverload.method(List.of(ValueType.RGB, ValueType.NUMBER),
                        "returns a copy of this RGB value with the red color channel exchanged with the given NUMBER.",
                        RgbFunctions::withRed)));
        registry.put(new FunctionGroup("r01",
                FunctionOverload.method(List.of(ValueType.RGB),
                        "returns the value of the red color channel projected to the range 0..1",
                        RgbFunctions::red01)));
        registry.put(new FunctionGroup("g",
                FunctionOverload.method(List.of(ValueType.RGB),
                        "returns the value of the green color channel. the normalized range is 0..255, but all other values are possible.",
                        RgbFunctions::green),
                FunctionOverload.method(List.of(ValueType.RGB, ValueType.NUMBER),
                        "returns a copy of this RGB value with the green color channel exchanged with the given NUMBER.",
                        RgbFunctions::withGreen)));
        registry.put(new FunctionGroup("g01",
                FunctionOverload.method(List.of(ValueType.RGB),
                        "returns the value of the green color channel projected to the range 0..1",
                        RgbFunctions::green01)));
        registry.put(new FunctionGroup("b",
                FunctionOverload.method(List.of(ValueType.RGB),
                        "returns the value of the blue color channel. the normalized range is 0..255, but all other values are possible.",
                        RgbFunctions::blue),
                FunctionOverload.method(List.of(ValueType.RGB, ValueType.NUMBER),
                        "returns a copy of this RGB value with the blue color channel exchanged with the given NUMBER.",
                        RgbFunctions::withBlue)));
        registry.put(new FunctionGroup("b01",
                FunctionOverload.method(List.of(ValueType.RGB),
                        "returns the value of the blue color channel projected to the range 0..1",
                        RgbFunctions::blue01)));
        registry.put(new FunctionGroup("a",
                FunctionOverload.method(List.of(ValueType.RGB),
                        "returns the value of the alpha channel. the normalized range is 0..255, but all other values are possible.",
                        RgbFunctions::alpha),
                FunctionOverload.method(List.of(ValueType.RGB, ValueType.NUMBER),
                        "returns a copy of this RGB value with the alpha channel exchanged with the given NUMBER.",
                        RgbFunctions::withAlpha)));
        registry.put(new FunctionGroup("a01",
                FunctionOverload.method(List.of(ValueType.RGB),
                        "returns the value of the alpha channel projected to the range 0..1",
                        RgbFunctions::alpha01),
                FunctionOverload.method(List.of(ValueType.RGB, ValueType.NUMBER),
                        "returns a copy of this RGB value with the alpha channel (range 0..1) exchanged with the given NUMBER.",
                        RgbFunctions::withAlpha01)));
        registry.put(new FunctionGroup("intensity",
                FunctionOverload.method(List.of(ValueType.RGB),
                        "returns the intensity of this RGB value. the normalized range is 0..255, but all values are possible.",
                        RgbFunctions::intensity)));
        registry.put(new FunctionGroup("i",
                FunctionOverload.method(List.of(ValueType.RGB),
                        "returns the intensity of this RGB value. the normalized range is 0..255, but all values are possible.",
                        RgbFunctions::intensity)));
        registry.put(new FunctionGroup("intensity01",
                FunctionOverload.method(List.of(ValueType.RGB),
                        "returns the intensity of this RGB value projected to the range 0..1",
                        RgbFunctions::intensity01)));
        registry.put(new FunctionGroup("i01",
                FunctionOverload.method(List.of(ValueType.RGB),
                        "returns the intensity of this RGB value projected to the range 0..1",
                        RgbFunctions::intensity01)));
        registry.put(new FunctionGroup("over",
                FunctionOverload.method(
                        List.of(ValueType.RGB, ValueType.RGB),
                        "applies the alpha-compositing function `over` to two RGB values, painting this RGB over the second RGB.",
                        RgbFunctions::rgbOverRgb),
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.IMAGE),
                        "applies the alpha-compositing function `over` to two IMAGEs, painting this IMAGE over the second IMAGE.",
                        RgbFunctions::imageOverImage)));
        registry.put(new FunctionGroup("grey",
                FunctionOverload.method(List.of(ValueType.RGB),
                        "returns an RGB value with all color channels set to the intensity of the given RGB, copying the alpha channel value",
                        RgbFunctions::greyscale)));
        registry.put(new FunctionGroup("hsv",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER),
                        "returns an HSV value constructed from the given hue, saturation and value.",
                        RgbFunctions::hsv),
                FunctionOverload.method(
                        List.of(ValueType.RGB),
                        "converts this RGB value to its HSV representation and returns the HSV, dropping the alpha channel.",
                        RgbFunctions::hsvFromRgb)));
        registry.put(new FunctionGroup("h",
                FunctionOverload.method(List.of(ValueType.HSV),
                        "returns the hue of this HSV in the range 0..360",
                        RgbFunctions::hue),
                FunctionOverload.method(List.of(ValueType.HSV, ValueType.NUMBER),
                        "returns a copy of this HSV with the hue exchanged with the given NUMBER",
                        RgbFunctions::withHue)));
        registry.put(new FunctionGroup("s",
                FunctionOverload.method(List.of(ValueType.HSV),
                        "returns the saturation of this HSV in the range 0..1",
                        RgbFunctions::saturation),
                FunctionOverload.method(List.of(ValueType.HSV, ValueType.NUMBER),
                        "returns a copy of this HSV with the saturation exchanged with the given NUMBER",
                        RgbFunctions::withSaturation)));
        registry.put(new FunctionGroup("v",
                FunctionOverload.method(List.of(ValueType.HSV),
                        "returns the value of this HSV in the range 0..1",
                        RgbFunctions::value),
                FunctionOverload.method(List.of(ValueType.HSV, ValueType.NUMBER),
                        "returns a copy of this HSV with the value exchanged with the given NUMBER",
                        RgbFunctions::withValue)));
        registry.put(new FunctionGroup("add_hue",
                FunctionOverload.method(List.of(ValueType.HSV, ValueType.NUMBER),
                        "returns a copy of this HSV with the given NUMBER added to the hue",
                        RgbFunctions::addHue)));
        registry.put(new FunctionGroup("add_saturation",
                FunctionOverload.method(List.of(ValueType.HSV, ValueType.NUMBER),
                        "returns a copy of this HSV with the given NUMBER added to the saturation",
                        RgbFunctions::addSaturation)));
        registry.put(new FunctionGroup("add_value",
                FunctionOverload.method(List.of(ValueType.HSV, ValueType.NUMBER),
                        "returns a copy of this HSV with the given NUMBER added to the value",
                        RgbFunctions::addValue)));
    }

    private static Value imageOverImage(List<Value> args) {
        final ImageVal i1 = (ImageVal) args.get(0);
        final ImageVal i2 = (ImageVal) args.get(1);
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
