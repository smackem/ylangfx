package net.smackem.ylang.execution.functions;

import net.smackem.ylang.runtime.*;

import java.util.List;

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

    private static Value imageClone(List<Value> args) {
        return new ImageVal((ImageVal) args.get(0));
    }

    private static Value plot(List<Value> args) {
        ((ImageVal) args.get(0)).plot((GeometryVal) args.get(1), (RgbVal) args.get(2));
        return NilVal.INSTANCE;
    }

    private static Value getClip(List<Value> args) {
        return RectVal.fromIntRect(((ImageVal) args.get(0)).getClipRect());
    }

    private static Value setClip(List<Value> args) {
        final ImageVal image = (ImageVal) args.get(0);
        final RectVal rect = (RectVal) args.get(1);
        final IntRect old = image.getClipRect();
        image.setClipRect(rect.round());
        return old != null
                ? RectVal.fromIntRect(old)
                : NilVal.INSTANCE;
    }

    private static Value getDefault(List<Value> args) {
        return ((ImageVal) args.get(0)).getDefaultPixel();
    }

    private static Value setDefault(List<Value> args) {
        final ImageVal image = (ImageVal) args.get(0);
        final Value old = image.getDefaultPixel();
        image.setDefaultPixel((RgbVal) args.get(1));
        return old != null
                ? old
                : NilVal.INSTANCE;
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
