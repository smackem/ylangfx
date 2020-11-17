package net.smackem.ylang.execution.functions;

import net.smackem.ylang.execution.ExecutionException;
import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.execution.operators.BinaryOperator;
import net.smackem.ylang.runtime.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CollectionFunctions {
    private CollectionFunctions() {}

    static void register(FunctionRegistry registry) {
        final List<FunctionOverload> listOverloads = ValueType.publicValues().stream()
                .map(initialValueType -> FunctionOverload.function(
                        List.of(ValueType.NUMBER, initialValueType),
                        CollectionFunctions::listCreateWith))
                .collect(Collectors.toList());
        listOverloads.add(FunctionOverload.function(
                List.of(ValueType.LIST),
                CollectionFunctions::listClone));
        listOverloads.add(FunctionOverload.function(
                List.of(ValueType.NUMBER),
                CollectionFunctions::listCreate));
        registry.put(new FunctionGroup("list", listOverloads));
        registry.put(new FunctionGroup("push",
                ValueType.publicValues().stream()
                        .map(right -> FunctionOverload.method(
                                List.of(ValueType.LIST, right),
                                CollectionFunctions::push))
                        .collect(Collectors.toList())));
        registry.put(new FunctionGroup("pop",
                FunctionOverload.method(
                        List.of(ValueType.LIST),
                        CollectionFunctions::pop)));
        registry.put(new FunctionGroup("removeAt",
                FunctionOverload.method(
                        List.of(ValueType.LIST, ValueType.NUMBER),
                        CollectionFunctions::removeAt)));
        registry.put(new FunctionGroup("reverse",
                FunctionOverload.method(
                        List.of(ValueType.LIST),
                        CollectionFunctions::reverse)));
        registry.put(new FunctionGroup("size",
                FunctionOverload.method(
                        List.of(ValueType.LIST),
                        CollectionFunctions::listSize),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL),
                        CollectionFunctions::kernelSize),
                FunctionOverload.method(
                        List.of(ValueType.MAP),
                        CollectionFunctions::mapSize)));
        registry.put(new FunctionGroup("kernel",
                FunctionOverload.function(
                        List.of(ValueType.RECT),
                        CollectionFunctions::kernelFromBounds),
                FunctionOverload.function(
                        List.of(ValueType.NUMBER, ValueType.NUMBER),
                        CollectionFunctions::kernelCreateFromWidthAndHeight),
                FunctionOverload.function(
                        List.of(ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER),
                        CollectionFunctions::kernelCreateWith),
                FunctionOverload.function(
                        List.of(ValueType.KERNEL),
                        CollectionFunctions::kernelClone),
                FunctionOverload.function(
                        List.of(ValueType.IMAGE),
                        CollectionFunctions::kernelFromImage)));
        registry.put(new FunctionGroup("gaussian",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        CollectionFunctions::gaussianKernel)));
        registry.put(new FunctionGroup("laplace",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        CollectionFunctions::laplaceKernel)));
        // kernel width and height are defined in CommonFunctions
        registry.put(new FunctionGroup("sum",
                FunctionOverload.method(
                        List.of(ValueType.LIST),
                        CollectionFunctions::listSum),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL),
                        CollectionFunctions::kernelSum)));
        registry.put(new FunctionGroup("sort",
                FunctionOverload.method(
                        List.of(ValueType.KERNEL),
                        CollectionFunctions::sortKernel),
                FunctionOverload.method(
                        List.of(ValueType.LIST),
                        CollectionFunctions::sortList),
                FunctionOverload.method(
                        List.of(ValueType.LIST, ValueType.FUNCTION),
                        CollectionFunctions::sortListByComparison)));
    }

    private static Value kernelCreateFromWidthAndHeight(List<Value> args) {
        final NumberVal width = (NumberVal) args.get(0);
        final NumberVal height = (NumberVal) args.get(1);
        return new KernelVal((int) (width.value() + 0.5f), (int) (height.value() + 0.5f), 0);
    }

    private static Value kernelFromBounds(List<Value> args) {
        final RectVal rect = (RectVal) args.get(0);
        return new KernelVal((int) rect.width(), (int) rect.height(), 0);
    }

    private static Value kernelFromImage(List<Value> args) {
        return KernelVal.fromImage((ImageVal) args.get(0));
    }

    private static Value sortListByComparison(List<Value> args) {
        final ListVal list = (ListVal) args.get(0);
        final FunctionVal compare = (FunctionVal) args.get(1);
        if (compare.parameterCount() != 2) {
            throw new IllegalArgumentException("the compare function must accept two arguments!");
        }
        list.sort((a, b) -> {
            final NumberVal result = (NumberVal) compare.invoke(List.of(a, b));
            return (int) result.value();
        });
        return list;
    }

    private static Value sortList(List<Value> args) {
        final ListVal list = (ListVal) args.get(0);
        list.sort((a, b) -> {
            final NumberVal result;
            try {
                result = (NumberVal) BinaryOperator.CMP.invoke(a, b);
            } catch (MissingOverloadException e) {
                throw new RuntimeException(e);
            }
            return (int) result.value();
        });
        return list;
    }

    private static Value sortKernel(List<Value> args) {
        final KernelVal kernel = (KernelVal) args.get(0);
        kernel.sort();
        return kernel;
    }

    private static Value laplaceKernel(List<Value> args) {
        return KernelVal.laplace((int) ((NumberVal) args.get(0)).value());
    }

    private static Value gaussianKernel(List<Value> args) {
        return KernelVal.gaussian((int) ((NumberVal) args.get(0)).value());
    }

    private static Value reverse(List<Value> args) {
        return ((ListVal) args.get(0)).reversed();
    }

    private static Value mapSize(List<Value> args) {
        return new NumberVal(((MapVal) args.get(0)).entries().size());
    }

    private static Value listSum(List<Value> args) {
        Value result = null;
        try {
            for (final Value v : (ListVal) args.get(0)) {
                if (result == null) {
                    result = v;
                } else {
                    result = BinaryOperator.ADD.invoke(result, v);
                }
            }
        } catch (MissingOverloadException e) {
            throw new RuntimeException(e);
        }
        return result != null ? result : NilVal.INSTANCE;
    }

    private static Value kernelSum(List<Value> args) {
        return ((KernelVal) args.get(0)).sum();
    }

    private static Value kernelClone(List<Value> args) {
        return new KernelVal((KernelVal) args.get(0));
    }

    private static Value kernelCreateWith(List<Value> args) {
        return new KernelVal(
                (int) ((NumberVal) args.get(0)).value(),
                (int) ((NumberVal) args.get(1)).value(),
                ((NumberVal) args.get(2)).value());
    }

    private static Value kernelSize(List<Value> args) {
        return new NumberVal(((KernelVal) args.get(0)).size());
    }

    private static Value listSize(List<Value> args) {
        return new NumberVal(((ListVal) args.get(0)).size());
    }

    private static Value removeAt(List<Value> args) {
        final NumberVal index = (NumberVal) args.get(1);
        return ((ListVal) args.get(0)).removeAt((int) index.value());
    }

    private static Value pop(List<Value> args) {
        final ListVal list = (ListVal) args.get(0);
        return list.removeLast();
    }

    private static Value push(List<Value> args) {
        final ListVal list = (ListVal) args.get(0);
        list.add(args.get(1));
        return list;
    }

    private static Value listCreate(List<Value> args) {
        final NumberVal size = (NumberVal) args.get(0);
        return new ListVal(Collections.nCopies((int) size.value(), NilVal.INSTANCE));
    }

    private static Value listClone(List<Value> args) {
        return new ListVal((ListVal) args.get(0));
    }

    private static Value listCreateWith(List<Value> args) {
        final NumberVal size = (NumberVal) args.get(0);
        final Value initialValue = args.get(1);
        return new ListVal(Collections.nCopies((int) size.value(), initialValue));
    }
}
