package net.smackem.ylang.execution.functions;

import net.smackem.ylang.runtime.*;

import java.util.List;

public class CommonFunctions {
    private CommonFunctions() {}

    static void register(FunctionRegistry registry) {
        registry.put(new FunctionGroup("width",
                FunctionOverload.method(
                        List.of(ValueType.RECT),
                        CommonFunctions::rectWidth),
                FunctionOverload.method(
                        List.of(ValueType.IMAGE),
                        CommonFunctions::imageWidth),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL),
                        CommonFunctions::kernelWidth)));
        registry.put(new FunctionGroup("height",
                FunctionOverload.method(
                        List.of(ValueType.RECT),
                        CommonFunctions::rectHeight),
                FunctionOverload.method(
                        List.of(ValueType.IMAGE),
                        CommonFunctions::imageHeight),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL),
                        CommonFunctions::kernelHeight)));
    }

    private static Value imageHeight(List<Value> args) {
        final ImageVal image = (ImageVal) args.get(0);
        return new NumberVal(image.height());
    }

    private static Value imageWidth(List<Value> args) {
        final ImageVal image = (ImageVal) args.get(0);
        return new NumberVal(image.width());
    }

    private static Value kernelHeight(List<Value> args) {
        final KernelVal kernel = (KernelVal) args.get(0);
        return new NumberVal(kernel.height());
    }

    private static Value kernelWidth(List<Value> args) {
        final KernelVal kernel = (KernelVal) args.get(0);
        return new NumberVal(kernel.width());
    }

    private static Value rectHeight(List<Value> args) {
        final RectVal rect = (RectVal) args.get(0);
        return new NumberVal(rect.height());
    }

    private static Value rectWidth(List<Value> args) {
        final RectVal rect = (RectVal) args.get(0);
        return new NumberVal(rect.width());
    }
}
