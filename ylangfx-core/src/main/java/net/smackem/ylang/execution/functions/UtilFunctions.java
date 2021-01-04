package net.smackem.ylang.execution.functions;

import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.execution.operators.BinaryOperator;
import net.smackem.ylang.runtime.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class UtilFunctions {
    private UtilFunctions() {}

    static void register(FunctionRegistry registry) {
        registry.put(new FunctionGroup("random",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER, ValueType.NUMBER),
                        "returns an integer random number in the range NUMBER-1..NUMBER-2]",
                        UtilFunctions::integerRandom),
                FunctionOverload.function(
                        Collections.emptyList(),
                        "returns a random number in the range 0..1",
                        UtilFunctions::floatRandom)));
        registry.put(new FunctionGroup("min",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER, ValueType.NUMBER),
                        "returns the smaller of the two given NUMBERs",
                        UtilFunctions::minNumber),
                FunctionOverload.function(
                        List.of(ValueType.RGB, ValueType.RGB),
                        "returns an RGB value with each color channel set to the minimum of the corresponding color channels in the two given RGBs",
                        UtilFunctions::minRgb),
                FunctionOverload.method(
                        List.of(ValueType.LIST),
                        "returns the minimum value in this LIST or `nil` if the list is empty",
                        UtilFunctions::minList),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL),
                        "returns the minimum value in this KERNEL",
                        UtilFunctions::minKernel),
                FunctionOverload.method(
                        List.of(ValueType.IMAGE),
                        "returns an RGB value composed of the minimum R, G, and B values in this IMAGE",
                        UtilFunctions::minImage),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL, ValueType.KERNEL), """
                            applies the `min(NUMBER, NUMBER)` operation to the pixels in the given KERNELs.
                            for binarized kernels, this is equivalent to the `AND` operation.
                            returns the resulting KERNEL.
                            """,
                        UtilFunctions::minKernelWithKernel),
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.IMAGE), """
                            applies the `min(RGB, RGB)` operation to the pixels in the given IMAGEs.
                            for binarized images, this is equivalent to the `AND` operation.
                            returns the resulting IMAGE.
                            """,
                        UtilFunctions::minImageWithImage)));
        registry.put(new FunctionGroup("max",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER, ValueType.NUMBER),
                        "returns the greater of the two given NUMBERs",
                        UtilFunctions::maxNumber),
                FunctionOverload.function(
                        List.of(ValueType.RGB, ValueType.RGB),
                        "returns an RGB value with each color channel set to the maximum of the corresponding color channels in the two given RGBs",
                        UtilFunctions::maxRgb),
                FunctionOverload.method(
                        List.of(ValueType.LIST),
                        "returns the maximum value in this LIST or `nil` if the list is empty",
                        UtilFunctions::maxList),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL),
                        "returns the maximum value in this KERNEL",
                        UtilFunctions::maxKernel),
                FunctionOverload.method(
                        List.of(ValueType.IMAGE),
                        "returns an RGB value composed of the maximum R, G, and B values in this IMAGE",
                        UtilFunctions::maxImage),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL, ValueType.KERNEL), """
                            applies the `max(NUMBER, NUMBER)` operation to the pixels in the given KERNELs.
                            for binarized kernels, this is equivalent to the `OR` operation.
                            returns the resulting KERNEL.
                            """,
                        UtilFunctions::maxKernelWithKernel),
                FunctionOverload.method(
                        List.of(ValueType.IMAGE, ValueType.IMAGE), """
                            applies the `max(RGB, RGB)` operation to the pixels in the given IMAGEs.
                            for binarized images, this is equivalent to the `OR` operation.
                            returns the resulting IMAGE.
                            """,
                        UtilFunctions::maxImageWithImage)));
    }

    private static Value maxImage(List<Value> args) {
        return ((ImageVal) args.get(0)).max();
    }

    private static Value minImage(List<Value> args) {
        return ((ImageVal) args.get(0)).min();
    }

    private static Value minImageWithImage(List<Value> args) {
        final ImageVal i1 = (ImageVal) args.get(0);
        final ImageVal i2 = (ImageVal) args.get(1);
        return ImageVal.min(i1, i2);
    }

    private static Value maxImageWithImage(List<Value> args) {
        final ImageVal i1 = (ImageVal) args.get(0);
        final ImageVal i2 = (ImageVal) args.get(1);
        return ImageVal.max(i1, i2);
    }

    private static Value minKernelWithKernel(List<Value> args) {
        final KernelVal k1 = (KernelVal) args.get(0);
        final KernelVal k2 = (KernelVal) args.get(1);
        return KernelVal.min(k1, k2);
    }

    private static Value maxKernelWithKernel(List<Value> args) {
        final KernelVal k1 = (KernelVal) args.get(0);
        final KernelVal k2 = (KernelVal) args.get(1);
        return KernelVal.max(k1, k2);
    }

    private static Value maxRgb(List<Value> args) {
        final RgbVal rgb1 = (RgbVal) args.get(0);
        final RgbVal rgb2 = (RgbVal) args.get(1);
        return RgbVal.max(rgb1, rgb2);
    }

    private static Value minRgb(List<Value> args) {
        final RgbVal rgb1 = (RgbVal) args.get(0);
        final RgbVal rgb2 = (RgbVal) args.get(1);
        return RgbVal.min(rgb1, rgb2);
    }

    private static Value maxKernel(List<Value> args) {
        final Value max = ((KernelVal) args.get(0)).max();
        return max != null ? max : NilVal.INSTANCE;
    }

    private static Value maxList(List<Value> args) {
        Value max = null;
        try {
            for (final Value v : (ListVal) args.get(0)) {
                if (max == null) {
                    max = v;
                } else {
                    final NumberVal n = (NumberVal) BinaryOperator.CMP.invoke(max, v);
                    if (n.value() < 0) {
                        max = v;
                    }
                }
            }
        } catch (MissingOverloadException e) {
            throw new RuntimeException(e);
        }
        return max != null ? max : NilVal.INSTANCE;
    }

    private static Value maxNumber(List<Value> args) {
        final NumberVal a = ((NumberVal) args.get(0));
        final NumberVal b = ((NumberVal) args.get(1));
        return NumberVal.max(a, b);
    }

    private static Value minKernel(List<Value> args) {
        final Value min = ((KernelVal) args.get(0)).min();
        return min != null ? min : NilVal.INSTANCE;
    }

    private static Value minList(List<Value> args) {
        Value min = null;
        try {
            for (final Value v : (ListVal) args.get(0)) {
                if (min == null) {
                    min = v;
                } else {
                    final NumberVal n = (NumberVal) BinaryOperator.CMP.invoke(min, v);
                    if (n.value() > 0) {
                        min = v;
                    }
                }
            }
        } catch (MissingOverloadException e) {
            throw new RuntimeException(e);
        }
        return min != null ? min : NilVal.INSTANCE;
    }

    private static Value minNumber(List<Value> args) {
        final NumberVal a = ((NumberVal) args.get(0));
        final NumberVal b = ((NumberVal) args.get(1));
        return NumberVal.min(a, b);
    }

    private static Value floatRandom(List<Value> args) {
        return new NumberVal(ThreadLocalRandom.current().nextFloat());
    }

    private static Value integerRandom(List<Value> args) {
        return new NumberVal(ThreadLocalRandom.current().nextInt((int) ((NumberVal) args.get(0)).value(), (int) ((NumberVal) args.get(1)).value()));
    }
}
