package net.smackem.ylang.execution;

import net.smackem.ylang.lang.DebugInfo;

public class ExecutionException extends Exception {

    private final DebugInfo debugInfo;

    ExecutionException(String message, DebugInfo debugInfo, Exception cause) {
        super(message, cause);
        this.debugInfo = debugInfo;
    }

    public DebugInfo debugInfo() {
        return this.debugInfo;
    }
}
