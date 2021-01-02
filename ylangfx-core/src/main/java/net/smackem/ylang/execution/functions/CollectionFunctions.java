package net.smackem.ylang.execution.functions;

import net.smackem.ylang.execution.MissingOverloadException;
import net.smackem.ylang.execution.operators.BinaryOperator;
import net.smackem.ylang.runtime.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CollectionFunctions {
    private CollectionFunctions() {}

    static void register(FunctionRegistry registry) {
        final List<FunctionOverload> listOverloads = ValueType.publicValues().stream()
                .map(initialValueType -> FunctionOverload.function(
                        List.of(ValueType.NUMBER, initialValueType),
                        "create a list of NUMBER repetitions of the second argument",
                        CollectionFunctions::listCreateWith))
                .collect(Collectors.toList());
        listOverloads.add(FunctionOverload.function(
                List.of(ValueType.LIST),
                "create a shallow clone of the specified LIST",
                CollectionFunctions::listClone));
        listOverloads.add(FunctionOverload.function(
                List.of(ValueType.NUMBER),
                "create a list of NUMBER `nil` values",
                CollectionFunctions::listCreate));
        registry.put(new FunctionGroup("list", listOverloads));
        registry.put(new FunctionGroup("push",
                ValueType.publicValues().stream()
                        .map(right -> FunctionOverload.method(
                                List.of(ValueType.LIST, right),
                                "adds the specified value to the end of the list",
                                CollectionFunctions::push))
                        .collect(Collectors.toList())));
        registry.put(new FunctionGroup("pop",
                FunctionOverload.method(
                        List.of(ValueType.LIST),
                        "removes and returns the last element in the list, throwing an exception if the list is empty",
                        CollectionFunctions::pop)));
        registry.put(new FunctionGroup("remove_at",
                FunctionOverload.method(
                        List.of(ValueType.LIST, ValueType.NUMBER),
                        "removes the element at index NUMBER (which is truncated to integer) from the given list",
                        CollectionFunctions::removeAt)));
        registry.put(new FunctionGroup("reverse",
                FunctionOverload.method(
                        List.of(ValueType.LIST),
                        "returns a shallow copy of the list with the order of elements reversed",
                        CollectionFunctions::reverse)));
        registry.put(new FunctionGroup("size",
                FunctionOverload.method(
                        List.of(ValueType.LIST),
                        "returns the number of elements in the list",
                        CollectionFunctions::listSize),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL),
                        "returns the number of elements in the kernel",
                        CollectionFunctions::kernelSize),
                FunctionOverload.method(
                        List.of(ValueType.MAP),
                        "returns the number of entries in the map",
                        CollectionFunctions::mapSize)));
        registry.put(new FunctionGroup("kernel",
                FunctionOverload.function(
                        List.of(ValueType.RECT),
                        "creates a new kernel with the given bounds and all elements set to 0",
                        CollectionFunctions::kernelFromBounds),
                FunctionOverload.function(
                        List.of(ValueType.NUMBER, ValueType.NUMBER),
                        "creates a new kernel with the given width and height, all elements set to 0",
                        CollectionFunctions::kernelCreateFromWidthAndHeight),
                FunctionOverload.function(
                        List.of(ValueType.NUMBER, ValueType.NUMBER, ValueType.NUMBER),
                        "creates a new kernel with the given width and height, all elements set to the value of the last argument",
                        CollectionFunctions::kernelCreateWith),
                FunctionOverload.function(
                        List.of(ValueType.KERNEL),
                        "creates a clone of the given kernel",
                        CollectionFunctions::kernelClone),
                FunctionOverload.function(
                        List.of(ValueType.IMAGE),
                        "creates a greyscale image (kernel) from the given image",
                        CollectionFunctions::kernelFromImage)));
        registry.put(new FunctionGroup("gaussian",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        "creates a gaussian kernel (for low-pass-filters) with the given radius",
                        CollectionFunctions::gaussianKernel)));
        registry.put(new FunctionGroup("laplacian",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        "creates a laplacian kernel (for high-pass-filters) with the given radius",
                        CollectionFunctions::laplacianKernel)));
        // kernel width and height are defined in CommonFunctions
        registry.put(new FunctionGroup("sum",
                FunctionOverload.method(
                        List.of(ValueType.LIST),
                        "returns the sum of all elements in the list, throwing an exception when encountering incompatible types",
                        CollectionFunctions::listSum),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL),
                        "returns the sum of all elements in the kernel",
                        CollectionFunctions::kernelSum)));
        registry.put(new FunctionGroup("sort",
                FunctionOverload.method(
                        List.of(ValueType.KERNEL),
                        "sorts the kernel, modifying it in-place. returns the given kernel.",
                        CollectionFunctions::sortKernel),
                FunctionOverload.method(
                        List.of(ValueType.LIST),
                        "sorts the list, modifying it in-place. returns the given list.",
                        CollectionFunctions::sortList),
                FunctionOverload.method(
                        List.of(ValueType.LIST, ValueType.FUNCTION), """
                                sorts the list, modifying it in-place with the given comparator function.
                                the comparator function must accept two elements `a` and `b` and return
                                0 if `a` and `b` are equal, a value greater than zero if `a` is greater than
                                `b` or a value less than zero if `a` is less than `b`. 
                                returns the given kernel.
                                """,
                        CollectionFunctions::sortListByComparison)));
        registry.put(new FunctionGroup("keys",
                FunctionOverload.method(
                        List.of(ValueType.MAP),
                        "returns a list of all keys in the given map",
                        CollectionFunctions::mapKeys)));
        registry.put(new FunctionGroup("values",
                FunctionOverload.method(
                        List.of(ValueType.MAP),
                        "returns a list of all values in the given map",
                        CollectionFunctions::mapValues)));
    }

    private static Value mapValues(List<Value> args) {
        final MapVal map = (MapVal) args.get(0);
        return new ListVal(new ArrayList<>(map.entries().values()));
    }

    private static Value mapKeys(List<Value> args) {
        final MapVal map = (MapVal) args.get(0);
        return new ListVal(new ArrayList<>(map.entries().keySet()));
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

    private static Value laplacianKernel(List<Value> args) {
        return KernelVal.laplacian((int) ((NumberVal) args.get(0)).value());
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

    static Value kernelClone(List<Value> args) {
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
