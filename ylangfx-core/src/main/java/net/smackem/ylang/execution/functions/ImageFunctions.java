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
                        List.of(ValueType.NUMBER, ValueType.NUMBER, ValueType.RGB),
                        ImageFunctions::imageCreateWith),
                FunctionOverload.function(
                        List.of(ValueType.IMAGE),
                        ImageFunctions::imageClone),
                FunctionOverload.function(
                        List.of(ValueType.KERNEL),
                        ImageFunctions::imageFromKernel)));
        registry.put(new FunctionGroup("bounds",
                FunctionOverload.method(
                        List.of(ValueType.IMAGE),
                        ImageFunctions::bounds),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL),
                        ImageFunctions::bounds)));
        registry.put(new FunctionGroup("convolve",
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.KERNEL),
                        ImageFunctions::convolveFullImage),
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.POINT, ValueType.KERNEL),
                        ImageFunctions::convolveImage),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL, ValueType.KERNEL),
                        ImageFunctions::convolveFullKernel),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL, ValueType.POINT, ValueType.KERNEL),
                        ImageFunctions::convolveKernel)));
        registry.put(new FunctionGroup("select",
                FunctionOverload.method(
                        List.of(ValueType.KERNEL, ValueType.POINT, ValueType.KERNEL),
                        ImageFunctions::selectKernelFromKernel)));
        registry.put(new FunctionGroup("select_alpha",
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.POINT, ValueType.KERNEL),
                        args -> selectKernelFromImage(args, RgbVal::a))));
        registry.put(new FunctionGroup("select_red",
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.POINT, ValueType.KERNEL),
                        args -> selectKernelFromImage(args, RgbVal::r))));
        registry.put(new FunctionGroup("select_green",
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.POINT, ValueType.KERNEL),
                        args -> selectKernelFromImage(args, RgbVal::g))));
        registry.put(new FunctionGroup("select_blue",
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.POINT, ValueType.KERNEL),
                        args -> selectKernelFromImage(args, RgbVal::b))));
        registry.put(new FunctionGroup("default",
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.RGB),
                        ImageFunctions::setDefault),
                FunctionOverload.method(
                        List.of(ValueType.IMAGE),
                        ImageFunctions::getDefault),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL, ValueType.NUMBER),
                        ImageFunctions::setDefault),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL),
                        ImageFunctions::getDefault)));
        registry.put(new FunctionGroup("clip",
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.RECT),
                        ImageFunctions::setClip),
                FunctionOverload.method(
                        List.of(ValueType.IMAGE),
                        ImageFunctions::getClip),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL, ValueType.RECT),
                        ImageFunctions::setClip),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL),
                        ImageFunctions::getClip)));
        registry.put(new FunctionGroup("plot",
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.RECT, ValueType.RGB),
                        ImageFunctions::plotImage),
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.CIRCLE, ValueType.RGB),
                        ImageFunctions::plotImage),
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.LINE, ValueType.RGB),
                        ImageFunctions::plotImage),
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.POLYGON, ValueType.RGB),
                        ImageFunctions::plotImage),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL, ValueType.RECT, ValueType.NUMBER),
                        ImageFunctions::plotKernel),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL, ValueType.CIRCLE, ValueType.NUMBER),
                        ImageFunctions::plotKernel),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL, ValueType.LINE, ValueType.NUMBER),
                        ImageFunctions::plotKernel),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL, ValueType.POLYGON, ValueType.NUMBER),
                        ImageFunctions::plotKernel)));
    }

    private static Value imageCreateWith(List<Value> args) {
        final NumberVal width = (NumberVal) args.get(0);
        final NumberVal height = (NumberVal) args.get(1);
        return new ImageVal((int)(width.value() + 0.5f), (int)(height.value() + 0.5f), (RgbVal) args.get(2));
    }

    private static Value convolveKernel(List<Value> args) {
        final KernelVal image = (KernelVal) args.get(0);
        final PointVal pt = (PointVal) args.get(1);
        final KernelVal kernel = (KernelVal) args.get(2);
        return new NumberVal(image.convolve((int) pt.x(), (int) pt.y(), kernel));
    }

    private static Value convolveFullKernel(List<Value> args) {
        final KernelVal image = (KernelVal) args.get(0);
        final KernelVal kernel = (KernelVal) args.get(1);
        return image.convolve(kernel);
    }

    private static Value imageFromKernel(List<Value> args) {
        return ImageVal.fromKernel((KernelVal) args.get(0));
    }

    private static Value convolveFullImage(List<Value> args) {
        final ImageVal image = (ImageVal) args.get(0);
        final KernelVal kernel = (KernelVal) args.get(1);
        return image.convolve(kernel);
    }

    private static Value selectKernelFromImage(List<Value> args, ToFloatFunction<RgbVal> selector) {
        final ImageVal image = (ImageVal) args.get(0);
        final PointVal pt = (PointVal) args.get(1);
        final KernelVal kernel = (KernelVal) args.get(2);
        return image.selectKernel((int) pt.x(), (int) pt.y(), kernel, selector);
    }

    private static Value selectKernelFromKernel(List<Value> args) {
        final KernelVal image = (KernelVal) args.get(0);
        final PointVal pt = (PointVal) args.get(1);
        final KernelVal kernel = (KernelVal) args.get(2);
        return image.selectKernel((int) pt.x(), (int) pt.y(), kernel, NumberVal::value);
    }

    private static Value imageClone(List<Value> args) {
        return new ImageVal((ImageVal) args.get(0));
    }

    private static Value plotImage(List<Value> args) {
        final ImageVal image = (ImageVal) args.get(0);
        //noinspection unchecked
        image.plot((GeometryVal<PointVal>) args.get(1), (RgbVal) args.get(2));
        return image;
    }

    private static Value plotKernel(List<Value> args) {
        final KernelVal image = (KernelVal) args.get(0);
        //noinspection unchecked
        image.plot((GeometryVal<PointVal>) args.get(1), (NumberVal) args.get(2));
        return image;
    }

    private static Value getClip(List<Value> args) {
        final IntRect clipRect = ((MatrixVal<?>) args.get(0)).getClipRect();
        return clipRect != null
                ? RectVal.fromIntRect(clipRect)
                : NilVal.INSTANCE;
    }

    private static Value setClip(List<Value> args) {
        final MatrixVal<?> image = (MatrixVal<?>) args.get(0);
        final RectVal rect = (RectVal) args.get(1);
        image.setClipRect(rect.round());
        return image;
    }

    private static Value getDefault(List<Value> args) {
        final Value defaultElement = ((MatrixVal<?>) args.get(0)).getDefaultElement();
        return defaultElement != null
                ? defaultElement
                : NilVal.INSTANCE;
    }

    @SuppressWarnings("unchecked")
    private static Value setDefault(List<Value> args) {
        final MatrixVal<Value> image = (MatrixVal<Value>) args.get(0);
        image.setDefaultElement(args.get(1));
        return image;
    }

    private static Value convolveImage(List<Value> args) {
        final ImageVal image = (ImageVal) args.get(0);
        final PointVal pt = (PointVal) args.get(1);
        final KernelVal kernel = (KernelVal) args.get(2);
        return image.convolve((int) pt.x(), (int) pt.y(), kernel);
    }

    private static Value bounds(List<Value> args) {
        final MatrixVal<?> matrix = (MatrixVal<?>) args.get(0);
        return new RectVal(0, 0, matrix.width(), matrix.height());
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
