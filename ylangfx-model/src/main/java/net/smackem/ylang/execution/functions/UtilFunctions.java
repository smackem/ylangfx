package net.smackem.ylang.execution.functions;

import net.smackem.ylang.execution.Context;
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
                        UtilFunctions::integerRandom),
                FunctionOverload.function(
                        Collections.emptyList(),
                        UtilFunctions::floatRandom)));
        registry.put(new FunctionGroup("min",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER, ValueType.NUMBER),
                        UtilFunctions::minNumber),
                FunctionOverload.method(
                        List.of(ValueType.LIST),
                        UtilFunctions::minList),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL),
                        UtilFunctions::minKernel)));
        registry.put(new FunctionGroup("max",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER, ValueType.NUMBER),
                        UtilFunctions::maxNumber),
                FunctionOverload.method(
                        List.of(ValueType.LIST),
                        UtilFunctions::maxList),
                FunctionOverload.method(
                        List.of(ValueType.KERNEL),
                        UtilFunctions::maxKernel)));
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
        return a.value() > b.value() ? a : b;
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
        return a.value() < b.value() ? a : b;
    }

    private static Value floatRandom(List<Value> args) {
        return new NumberVal(ThreadLocalRandom.current().nextFloat());
    }

    private static Value integerRandom(List<Value> args) {
        return new NumberVal(ThreadLocalRandom.current().nextInt((int) ((NumberVal) args.get(0)).value(), (int) ((NumberVal) args.get(1)).value()));
    }
}
