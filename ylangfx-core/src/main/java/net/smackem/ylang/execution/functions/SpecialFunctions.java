package net.smackem.ylang.execution.functions;

import net.smackem.ylang.runtime.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SpecialFunctions {
    private SpecialFunctions() {}

    static void register(FunctionRegistry registry) {
        final List<FunctionOverload> setAtOverloads = new ArrayList<>();
        final Collection<ValueType> publicValueTypes = ValueType.publicValues();
        for (final ValueType rvalueType : publicValueTypes) {
            setAtOverloads.add(FunctionOverload.function(
                    List.of(ValueType.LIST, ValueType.NUMBER, rvalueType),
                    null,
                    SpecialFunctions::setListAtIndex));
            for (final ValueType keyValueType : publicValueTypes) {
                setAtOverloads.add(FunctionOverload.function(
                        List.of(ValueType.MAP, keyValueType, rvalueType),
                        null,
                        SpecialFunctions::setMapAtKey));
            }
        }
        setAtOverloads.add(FunctionOverload.function(
                List.of(ValueType.KERNEL, ValueType.NUMBER, ValueType.NUMBER),
                null,
                SpecialFunctions::setKernelAtIndex));
        setAtOverloads.add(FunctionOverload.function(
                List.of(ValueType.KERNEL, ValueType.POINT, ValueType.NUMBER),
                null,
                SpecialFunctions::setKernelAtPoint));
        setAtOverloads.add(FunctionOverload.function(
                List.of(ValueType.IMAGE, ValueType.POINT, ValueType.RGB),
                null,
                SpecialFunctions::setImageAtPoint));
        registry.put(new FunctionGroup(FunctionRegistry.FUNCTION_NAME_SET_AT, setAtOverloads));
    }

    private static Value setMapAtKey(List<Value> args) {
        final MapVal map = (MapVal) args.get(0);
        final Value key = args.get(1);
        map.entries().put(key, args.get(2));
        return null;
    }

    private static Value setImageAtPoint(List<Value> args) {
        final PointVal pt = (PointVal) args.get(1);
        final RgbVal rgb = (RgbVal) args.get(2);
        ((ImageVal) args.get(0)).set((int)pt.x(), (int)pt.y(), rgb);
        return null;
    }

    private static Value setKernelAtPoint(List<Value> args) {
        final KernelVal kernel = (KernelVal) args.get(0);
        final PointVal pt = (PointVal) args.get(1);
        final NumberVal n = (NumberVal) args.get(2);
        kernel.set((int) pt.x(), (int) pt.y(), n);
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
