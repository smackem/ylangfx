package net.smackem.ylang.execution.functions;

import net.smackem.ylang.runtime.Value;

import java.util.List;

@FunctionalInterface
public interface Func {
    Value invoke(List<Value> arguments);
}
