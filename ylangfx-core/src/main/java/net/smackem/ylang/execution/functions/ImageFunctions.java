package net.smackem.ylang.execution.functions;

import net.smackem.ylang.runtime.*;

import java.util.List;
import java.util.function.Function;

public class ImageFunctions {
    private ImageFunctions() {}

    static void register(FunctionRegistry registry) {
        registry.put(new FunctionGroup("image",
                FunctionOverload.function(
                        List.of(ValueType.RECT),
                        ImageFunctions::imageFromRect),
                FunctionOverload.function(
                        List.of(ValueType.NUMBER, ValueType.NUMBER),
                        ImageFunctions::imageFromWidthAndHeight),
                FunctionOverload.function(
                        List.of(ValueType.IMAGE),
                        ImageFunctions::imageClone)));
        registry.put(new FunctionGroup("bounds",
                FunctionOverload.method(
                        List.of(ValueType.IMAGE),
                        ImageFunctions::bounds)));
        registry.put(new FunctionGroup("convolve",
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.POINT, ValueType.KERNEL),
                        ImageFunctions::convolve)));
        registry.put(new FunctionGroup("selectAlpha",
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.POINT, ValueType.KERNEL),
                        args -> selectKernel(args, RgbVal::a))));
        registry.put(new FunctionGroup("selectRed",
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.POINT, ValueType.KERNEL),
                        args -> selectKernel(args, RgbVal::r))));
        registry.put(new FunctionGroup("selectGreen",
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.POINT, ValueType.KERNEL),
                        args -> selectKernel(args, RgbVal::g))));
        registry.put(new FunctionGroup("selectBlue",
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.POINT, ValueType.KERNEL),
                        args -> selectKernel(args, RgbVal::b))));
        registry.put(new FunctionGroup("default",
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.RGB),
                        ImageFunctions::setDefault),
                FunctionOverload.method(
                        List.of(ValueType.IMAGE),
                        ImageFunctions::getDefault)));
        registry.put(new FunctionGroup("clip",
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.RECT),
                        ImageFunctions::setClip),
                FunctionOverload.method(
                        List.of(ValueType.IMAGE),
                        ImageFunctions::getClip)));
        registry.put(new FunctionGroup("plot",
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.RECT, ValueType.RGB),
                        ImageFunctions::plot),
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.CIRCLE, ValueType.RGB),
                        ImageFunctions::plot),
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.LINE, ValueType.RGB),
                        ImageFunctions::plot),
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.POLYGON, ValueType.RGB),
                        ImageFunctions::plot)));
    }

    private static Value selectKernel(List<Value> args, ToFloatFunction<RgbVal> selector) {
        final ImageVal image = (ImageVal) args.get(0);
        final PointVal pt = (PointVal) args.get(1);
        final KernelVal kernel = (KernelVal) args.get(2);
        return image.selectKernel((int) pt.x(), (int) pt.y(), kernel, selector);
    }

    private static Value imageClone(List<Value> args) {
        return new ImageVal((ImageVal) args.get(0));
    }

    private static Value plot(List<Value> args) {
        ((ImageVal) args.get(0)).plot((GeometryVal) args.get(1), (RgbVal) args.get(2));
        return NilVal.INSTANCE;
    }

    private static Value getClip(List<Value> args) {
        final IntRect clipRect = ((ImageVal) args.get(0)).getClipRect();
        return clipRect != null
                ? RectVal.fromIntRect(clipRect)
                : NilVal.INSTANCE;
    }

    private static Value setClip(List<Value> args) {
        final ImageVal image = (ImageVal) args.get(0);
        final RectVal rect = (RectVal) args.get(1);
        image.setClipRect(rect.round());
        return image;
    }

    private static Value getDefault(List<Value> args) {
        final RgbVal defaultRgb = ((ImageVal) args.get(0)).getDefaultPixel();
        return defaultRgb != null
                ? defaultRgb
                : NilVal.INSTANCE;
    }

    private static Value setDefault(List<Value> args) {
        final ImageVal image = (ImageVal) args.get(0);
        image.setDefaultPixel((RgbVal) args.get(1));
        return image;
    }

    private static Value convolve(List<Value> args) {
        final ImageVal image = (ImageVal) args.get(0);
        final PointVal pt = (PointVal) args.get(1);
        final KernelVal kernel = (KernelVal) args.get(2);
        return image.convolve((int) pt.x(), (int) pt.y(), kernel);
    }

    private static Value bounds(List<Value> args) {
        final ImageVal image = (ImageVal) args.get(0);
        return new RectVal(0, 0, image.width(), image.height());
    }

    private static Value imageFromWidthAndHeight(List<Value> args) {
        final NumberVal width = (NumberVal) args.get(0);
        final NumberVal height = (NumberVal) args.get(1);
        return new ImageVal((int)(width.value() + 0.5f), (int)(height.value() + 0.5f));
    }

    private static Value imageFromRect(List<Value> args) {
        final RectVal rect = (RectVal) args.get(0);
        return new ImageVal((int)(rect.width() + 0.5f), (int)(rect.height() + 0.5f));
    }
}