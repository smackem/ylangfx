package net.smackem.ylang.execution.functions;

import net.smackem.ylang.runtime.*;

import java.util.ArrayList;
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
                        ImageFunctions::imageFromWidthAndHeight)));
        registry.put(new FunctionGroup("bounds",
                FunctionOverload.function(
                        List.of(ValueType.IMAGE),
                        ImageFunctions::bounds)));
        registry.put(new FunctionGroup("convolute",
                FunctionOverload.function(
                        List.of(ValueType.IMAGE, ValueType.POINT, ValueType.KERNEL),
                        ImageFunctions::convolute)));
    }

    private static Value convolute(List<Value> args) {
        final ImageVal image = (ImageVal) args.get(0);
        final PointVal pt = (PointVal) args.get(1);
        final KernelVal kernel = (KernelVal) args.get(2);
        return image.convolute((int) pt.x(), (int) pt.y(), kernel);
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
