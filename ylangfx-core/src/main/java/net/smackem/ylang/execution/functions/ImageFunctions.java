package net.smackem.ylang.execution.functions;

import net.smackem.ylang.runtime.*;

import java.util.List;

public class ImageFunctions {
    private ImageFunctions() {}

    static void register(FunctionRegistry registry) {
        registry.put(new FunctionGroup("image",
                FunctionOverload.function(
                        List.of(ValueType.RECT), """
                            creates an IMAGE with the dimensions of the given RECT.
                            all pixels of the new image are initialized to `empty` (transparent black).
                            """,
                        ImageFunctions::imageFromRect),
                FunctionOverload.function(
                        List.of(ValueType.NUMBER, ValueType.NUMBER), """
                            creates an IMAGE with the given width and height.
                            all pixels of the new image are initialized to `empty` (transparent black).
                            """,
                        ImageFunctions::imageFromWidthAndHeight),
                FunctionOverload.function(
                        List.of(ValueType.NUMBER, ValueType.NUMBER, ValueType.RGB), """
                            creates an IMAGE with the dimensions of the given RECT.
                            all pixels of the new image are initialized to the given RGB value.
                            """,
                        ImageFunctions::imageCreateWith),
                FunctionOverload.function(
                        List.of(ValueType.IMAGE),
                        "clones the given image and returns the clone",
                        ImageFunctions::imageClone),
                FunctionOverload.function(
                        List.of(ValueType.KERNEL),
                        "creates a greyscale IMAGE with the dimensions and pixels of the given KERNEL",
                        ImageFunctions::imageFromKernel)));
        registry.put(new FunctionGroup("convolve",
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.KERNEL), """
                            apply the convolution operation to the image, using the given KERNEL as structuring element.
                            returns the convolved IMAGE.
                            """,
                        ImageFunctions::convolveFullImage),
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.POINT, ValueType.KERNEL), """
                            apply the convolution operation to the image at the specified POINT with the given KERNEL
                            as structuring element.
                            returns the resulting color as RGB.
                            """,
                        ImageFunctions::convolveImage),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL, ValueType.KERNEL),"""
                            apply the convolution operation to the kernel, using the given KERNEL as structuring element.
                            returns the convolved KERNEL.
                            """,
                        ImageFunctions::convolveFullKernel),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL, ValueType.POINT, ValueType.KERNEL),"""
                            apply the convolution operation to the kernel at the specified POINT with the given KERNEL
                            as structuring element.
                            returns the resulting color as NUMBER.
                            """,
                        ImageFunctions::convolveKernel)));
        registry.put(new FunctionGroup("clone",
                FunctionOverload.method(
                        List.of(ValueType.IMAGE), """
                            clones the given IMAGE.
                            `IMAGE.clone()` is equivalent to `image(IMAGE)`.
                            prefer `clone` methods if you want the code to work with KERNEL as well as IMAGE.
                            """,
                        ImageFunctions::imageClone),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL),"""
                            clones the given KERNEL.
                            `KERNEL.clone()` is equivalent to `kernel(KERNEL)`.
                            prefer `clone` methods if you want the code to work with KERNEL as well as IMAGE.
                            """,
                        CollectionFunctions::kernelClone)));
        registry.put(new FunctionGroup("clone_empty",
                FunctionOverload.method(
                        List.of(ValueType.IMAGE),"""
                            creates a new IMAGE with the same dimensions as the given IMAGE.
                            the pixels of the new image are initialized to `empty` (transparent black).
                            prefer `clone` methods if you want the code to work with KERNEL as well as IMAGE.
                            """,
                        ImageFunctions::imageCloneEmpty),
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.RECT),"""
                            creates a new IMAGE with the dimensions of the given RECT.
                            the pixels of the new image are initialized to `empty` (transparent black).
                            prefer `clone` methods if you want the code to work with KERNEL as well as IMAGE.
                            """,
                        ImageFunctions::imageCloneEmptyWithBounds),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL),"""
                            creates a new KERNEL with the same dimensions as the given KERNEL.
                            the pixels of the new kernel are initialized to `0` (black).
                            prefer `clone` methods if you want the code to work with KERNEL as well as IMAGE.
                            """,
                        ImageFunctions::kernelCloneEmpty),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL, ValueType.RECT),"""
                            creates a new KERNEL with the dimensions of the given RECT.
                            the pixels of the new kernel are initialized to `0` (black).
                            prefer `clone` methods if you want the code to work with KERNEL as well as IMAGE.
                            """,
                        ImageFunctions::kernelCloneEmptyWithBounds)));
        registry.put(new FunctionGroup("select",
                FunctionOverload.method(
                        List.of(ValueType.KERNEL, ValueType.POINT, ValueType.KERNEL), """
                            copies the area around POINT, multiplying the area pixel-wise
                            with the given KERNEL.
                            this is similar to convolution, but does not sum up the pixel values
                            of the selected area, but returns the entire area in a new KERNEL.
                            example:
                            `area := kernel(5, 5, 1)`
                            `selection := KERNEL.select(100;100, area)`
                            `selection` now contains the KERNEL's pixel values in the 5x5 area around 100;100.  
                            """,
                        ImageFunctions::selectKernelFromKernel)));
        registry.put(new FunctionGroup("select_alpha",
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.POINT, ValueType.KERNEL), """
                            selects the alpha values of the area around POINT, multiplied with the values in the given
                            KERNEL. returns a new KERNEL.
                            """,
                        args -> selectKernelFromImage(args, RgbVal::a))));
        registry.put(new FunctionGroup("select_red",
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.POINT, ValueType.KERNEL),"""
                            selects the red values of the area around POINT, multiplied with the values in the given
                            KERNEL. returns a new KERNEL.
                            """,
                        args -> selectKernelFromImage(args, RgbVal::r))));
        registry.put(new FunctionGroup("select_green",
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.POINT, ValueType.KERNEL),"""
                            selects the green values of the area around POINT, multiplied with the values in the given
                            KERNEL. returns a new KERNEL.
                            """,
                        args -> selectKernelFromImage(args, RgbVal::g))));
        registry.put(new FunctionGroup("select_blue",
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.POINT, ValueType.KERNEL),"""
                            selects the blue values of the area around POINT, multiplied with the values in the given
                            KERNEL. returns a new KERNEL.
                            """,
                        args -> selectKernelFromImage(args, RgbVal::b))));
        registry.put(new FunctionGroup("default",
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.RGB), """
                            sets the default RGB value of the given IMAGE.
                            everytime the IMAGE is queried for a pixel with coordinates which are
                            out of bounds, the `default` RGB is returned.
                            returns the given IMAGE to support fluent image initialization:
                            `img := image(w, h).clip(clip_rect).default(#000000)`
                            """,
                        ImageFunctions::setDefault),
                FunctionOverload.method(
                        List.of(ValueType.IMAGE),
                        "gets the default RGB value of the given IMAGE.",
                        ImageFunctions::getDefault),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL, ValueType.NUMBER),"""
                            sets the default NUMBER value of the given KERNEL.
                            everytime the KERNEL is queried for a pixel with coordinates which are
                            out of bounds, the `default` NUMBER is returned.
                            returns the given KERNEL to support fluent image initialization:
                            `img := kernel(w, h, 0).clip(clip_rect).default(0)`
                            """,
                        ImageFunctions::setDefault),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL),
                        "gets the default NUMBER value of the given KERNEL.",
                        ImageFunctions::getDefault)));
        registry.put(new FunctionGroup("clip",
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.RECT), """
                            sets the clip rect for the given IMAGE.
                            when setting pixels on the IMAGE, all coordinates outside the
                            clip rect are ignored.
                            returns the given IMAGE to support fluent image initialization:
                            `img := image(w, h).clip(clip_rect).default(#000000)`
                            """,
                        ImageFunctions::setClip),
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.NIL), """
                            removes the clip rect for the given IMAGE:
                            `img.clip(nil)`
                            """,
                        ImageFunctions::removeClip),
                FunctionOverload.method(
                        List.of(ValueType.IMAGE),
                        "returns the current clip rect of the given image or `nil` if none is set.",
                        ImageFunctions::getClip),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL, ValueType.RECT), """
                            sets the clip rect for the given KERNEL.
                            when setting pixels on the KERNEL, all coordinates outside the
                            clip rect are ignored.
                            returns the given KERNEL to support fluent image initialization:
                            `img := kernel(w, h, 0).clip(clip_rect).default(0)`
                            """,
                        ImageFunctions::setClip),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL, ValueType.NIL), """
                            removes the clip rect for the given KERNEL:
                            `knl.clip(nil)`
                            """,
                        ImageFunctions::removeClip),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL),
                        "returns the current clip rect of the given kernel or `nil` if none is set.",
                        ImageFunctions::getClip)));
        registry.put(new FunctionGroup("plot",
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.RECT, ValueType.RGB), """
                            plots the given geometry to this IMAGE, filling it with the specified RGB
                            returns this IMAGE to allow fluent plotting.
                            """,
                        ImageFunctions::plotImage),
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.CIRCLE, ValueType.RGB), """
                            plots the given geometry to this IMAGE, filling it with the specified RGB
                            returns this IMAGE to allow fluent plotting.
                            """,
                        ImageFunctions::plotImage),
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.LINE, ValueType.RGB), """
                            plots the given geometry to this IMAGE, filling it with the specified RGB
                            returns this IMAGE to allow fluent plotting.
                            """,
                        ImageFunctions::plotImage),
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.POLYGON, ValueType.RGB), """
                            plots the given geometry to this IMAGE, filling it with the specified RGB
                            returns this IMAGE to allow fluent plotting.
                            """,
                        ImageFunctions::plotImage),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL, ValueType.RECT, ValueType.NUMBER), """
                            plots the given geometry to this KERNEL, filling it with the specified NUMBER
                            returns this KERNEL to allow fluent plotting.
                            """,
                        ImageFunctions::plotKernel),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL, ValueType.CIRCLE, ValueType.NUMBER), """
                            plots the given geometry to this KERNEL, filling it with the specified NUMBER
                            returns this KERNEL to allow fluent plotting.
                            """,
                        ImageFunctions::plotKernel),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL, ValueType.LINE, ValueType.NUMBER), """
                            plots the given geometry to this KERNEL, filling it with the specified NUMBER
                            returns this KERNEL to allow fluent plotting.
                            """,
                        ImageFunctions::plotKernel),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL, ValueType.POLYGON, ValueType.NUMBER), """
                            plots the given geometry to this KERNEL, filling it with the specified NUMBER
                            returns this KERNEL to allow fluent plotting.
                            """,
                        ImageFunctions::plotKernel)));
    }

    private static Value kernelCloneEmptyWithBounds(List<Value> args) {
        final RectVal rect = (RectVal) args.get(1);
        return new KernelVal(Math.round(rect.width()), Math.round(rect.height()), 0);
    }

    private static Value imageCloneEmptyWithBounds(List<Value> args) {
        final RectVal rect = (RectVal) args.get(1);
        return new ImageVal(Math.round(rect.width()), Math.round(rect.height()));
    }

    private static Value kernelCloneEmpty(List<Value> args) {
        final KernelVal kernel = (KernelVal) args.get(0);
        return new KernelVal(kernel.width(), kernel.height(), 0);
    }

    private static Value imageCloneEmpty(List<Value> args) {
        final ImageVal image = (ImageVal) args.get(0);
        return new ImageVal(image.width(), image.height());
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

    private static Value removeClip(List<Value> args) {
        final MatrixVal<?> image = (MatrixVal<?>) args.get(0);
        image.setClipRect(null);
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
