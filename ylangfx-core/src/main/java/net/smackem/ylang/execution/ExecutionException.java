package net.smackem.ylang.execution;

import net.smackem.ylang.lang.Instruction;

import java.io.Serial;
import java.util.Collection;

public class ExecutionException extends Exception {
    @Serial
    private static final long serialVersionUID = 6705743L;

    private final Collection<Instruction> stackTrace;

    ExecutionException(String message, Collection<Instruction> stackTrace, Exception cause) {
        super(message, cause);
        this.stackTrace = stackTrace;
    }

    public Collection<Instruction> stackTrace() {
        return this.stackTrace;
    }
}
