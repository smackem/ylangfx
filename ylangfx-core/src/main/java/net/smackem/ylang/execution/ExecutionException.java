package net.smackem.ylang.execution;

import net.smackem.ylang.lang.Instruction;

import java.util.Collection;

public class ExecutionException extends Exception {

    private final Collection<Instruction> stackTrace;

    ExecutionException(String message, Collection<Instruction> stackTrace, Exception cause) {
        super(message, cause);
        this.stackTrace = stackTrace;
    }

    public Collection<Instruction> stackTrace() {
        return this.stackTrace;
    }
}
