package net.smackem.ylang.execution.functions;

import net.smackem.ylang.runtime.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CommonFunctions {
    private CommonFunctions() {}

    @SuppressWarnings("DuplicatedCode")
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
        final Collection<ValueType> publicTypes = ValueType.publicValues();
        final int typeCount = publicTypes.size();
        final List<FunctionOverload> typeOverloads = new ArrayList<>(typeCount);
        final List<FunctionOverload> isMatrixOverloads = new ArrayList<>(typeCount);
        final List<FunctionOverload> isGeometryOverloads = new ArrayList<>(typeCount);
        final List<FunctionOverload> isIterableOverloads = new ArrayList<>(typeCount);
        for (final ValueType valueType : publicTypes) {
            typeOverloads.add(FunctionOverload.method(List.of(valueType), CommonFunctions::type));
            isMatrixOverloads.add(FunctionOverload.method(List.of(valueType), CommonFunctions::isMatrix));
            isGeometryOverloads.add(FunctionOverload.method(List.of(valueType), CommonFunctions::isGeometry));
            isIterableOverloads.add(FunctionOverload.method(List.of(valueType), CommonFunctions::isIterable));
        }
        registry.put(new FunctionGroup("type", typeOverloads));
        registry.put(new FunctionGroup("is_matrix", isMatrixOverloads));
        registry.put(new FunctionGroup("is_geometry", isGeometryOverloads));
        registry.put(new FunctionGroup("is_iterable", isIterableOverloads));
    }

    private static Value isIterable(List<Value> args) {
        return BoolVal.of(args.get(0).type().isIterable());
    }

    private static Value isGeometry(List<Value> args) {
        return BoolVal.of(args.get(0).type().isGeometry());
    }

    private static Value isMatrix(List<Value> args) {
        return BoolVal.of(args.get(0).type().isMatrix());
    }

    private static Value type(List<Value> args) {
        return new StringVal(args.get(0).type().name());
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
