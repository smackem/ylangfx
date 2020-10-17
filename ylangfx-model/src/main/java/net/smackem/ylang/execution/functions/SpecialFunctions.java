package net.smackem.ylang.execution.functions;

import net.smackem.ylang.runtime.*;

import java.util.ArrayList;
import java.util.List;

public class SpecialFunctions {
    private SpecialFunctions() {}

    static void register(FunctionRegistry registry) {
        final List<FunctionOverload> setAtOverloads = new ArrayList<>();
        for (final ValueType valueType : ValueType.publicValues()) {
            setAtOverloads.add(FunctionOverload.function(
                    List.of(ValueType.LIST, ValueType.NUMBER, valueType),
                    SpecialFunctions::setListAtIndex));
        }
        setAtOverloads.add(FunctionOverload.function(
                List.of(ValueType.KERNEL, ValueType.NUMBER, ValueType.NUMBER),
                SpecialFunctions::setKernelAtIndex));
        setAtOverloads.add(FunctionOverload.function(
                List.of(ValueType.KERNEL, ValueType.POINT, ValueType.NUMBER),
                SpecialFunctions::setKernelAtPoint));
        setAtOverloads.add(FunctionOverload.function(
                List.of(ValueType.IMAGE, ValueType.POINT, ValueType.RGB),
                SpecialFunctions::setImageAtPoint));
        registry.put(new FunctionGroup(FunctionRegistry.FUNCTION_NAME_SET_AT, setAtOverloads));
    }

    private static Value setImageAtPoint(List<Value> args) {
        final PointVal pt = (PointVal) args.get(1);
        final RgbVal rgb = (RgbVal) args.get(2);
        ((ImageVal) args.get(0)).setPixel((int)pt.x(), (int)pt.y(), rgb);
        return null;
    }

    private static Value setKernelAtPoint(List<Value> args) {
        final KernelVal kernel = (KernelVal) args.get(0);
        final PointVal pt = (PointVal) args.get(1);
        final NumberVal n = (NumberVal) args.get(2);
        kernel.set((int) (pt.y() * kernel.width() + pt.x()), n);
        return null;
    }

    private static Value setKernelAtIndex(List<Value> args) {
        final KernelVal kernel = (KernelVal) args.get(0);
        final NumberVal index = (NumberVal) args.get(1);
        final NumberVal n = (NumberVal) args.get(2);
        kernel.set((int) index.value(), n);
        return null;
    }

    private static Value setListAtIndex(List<Value> args) {
        final ListVal list = (ListVal) args.get(0);
        final NumberVal index = (NumberVal) args.get(1);
        list.set((int) index.value(), args.get(2));
        return null;
    }
}
