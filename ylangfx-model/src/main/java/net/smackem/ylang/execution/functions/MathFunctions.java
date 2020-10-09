package net.smackem.ylang.execution.functions;

import net.smackem.ylang.runtime.NumberVal;
import net.smackem.ylang.runtime.Value;
import net.smackem.ylang.runtime.ValueType;

import java.util.List;

public class MathFunctions {
    private MathFunctions() {}

    static void register(FunctionRegistry registry) {
        registry.put(new FunctionGroup("sin",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        MathFunctions::sin)));
        registry.put(new FunctionGroup("cos",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        MathFunctions::cos)));
        registry.put(new FunctionGroup("tan",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        MathFunctions::tan)));
        registry.put(new FunctionGroup("asin",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        MathFunctions::asin)));
        registry.put(new FunctionGroup("acos",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        MathFunctions::acos)));
        registry.put(new FunctionGroup("atan",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        MathFunctions::atan)));
        registry.put(new FunctionGroup("atan2",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER, ValueType.NUMBER),
                        MathFunctions::atan2)));
        registry.put(new FunctionGroup("sqrt",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        MathFunctions::sqrt)));
        registry.put(new FunctionGroup("pow",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        MathFunctions::pow)));
        registry.put(new FunctionGroup("abs",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        MathFunctions::abs)));
        registry.put(new FunctionGroup("round",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        MathFunctions::round)));
        registry.put(new FunctionGroup("floor",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        MathFunctions::floor)));
        registry.put(new FunctionGroup("ceil",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        MathFunctions::ceil)));
        registry.put(new FunctionGroup("trunc",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER),
                        MathFunctions::floor)));
        registry.put(new FunctionGroup("hypot",
                FunctionOverload.function(
                        List.of(ValueType.NUMBER, ValueType.NUMBER),
                        MathFunctions::hypot)));
    }

    private static Value hypot(List<Value> args) {
        return new NumberVal((float) Math.hypot(((NumberVal) args.get(0)).value(), ((NumberVal) args.get(1)).value()));
    }

    private static Value ceil(List<Value> args) {
        return new NumberVal((float) Math.ceil(((NumberVal) args.get(0)).value()));
    }

    private static Value floor(List<Value> args) {
        return new NumberVal((float) Math.floor(((NumberVal) args.get(0)).value()));
    }

    private static Value round(List<Value> args) {
        return new NumberVal((float) Math.round(((NumberVal) args.get(0)).value()));
    }

    private static Value abs(List<Value> args) {
        return new NumberVal(Math.abs(((NumberVal) args.get(0)).value()));
    }

    private static Value pow(List<Value> args) {
        return new NumberVal((float) Math.pow(((NumberVal) args.get(0)).value(), ((NumberVal) args.get(1)).value()));
    }

    private static Value sqrt(List<Value> args) {
        return new NumberVal((float) Math.sqrt(((NumberVal) args.get(0)).value()));
    }

    private static Value atan2(List<Value> args) {
        return new NumberVal((float) Math.atan2(((NumberVal) args.get(0)).value(), ((NumberVal) args.get(1)).value()));
    }

    private static Value atan(List<Value> args) {
        return new NumberVal((float) Math.atan(((NumberVal) args.get(0)).value()));
    }

    private static Value acos(List<Value> args) {
        return new NumberVal((float) Math.acos(((NumberVal) args.get(0)).value()));
    }

    private static Value asin(List<Value> args) {
        return new NumberVal((float) Math.asin(((NumberVal) args.get(0)).value()));
    }

    private static Value tan(List<Value> args) {
        return new NumberVal((float) Math.tan(((NumberVal) args.get(0)).value()));
    }

    private static Value cos(List<Value> args) {
        return new NumberVal((float) Math.cos(((NumberVal) args.get(0)).value()));
    }

    private static Value sin(List<Value> args) {
        return new NumberVal((float) Math.sin(((NumberVal) args.get(0)).value()));
    }
}
