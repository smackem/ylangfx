package net.smackem.ylang.lang;

/**
 * Lists built-in functions that the compiler must be aware of.
 */
public interface FunctionTable {
    /**
     * Returns the name of the function used for {@code x[y] = z}.
     */
    String indexAssignmentFunction();

    /**
     * Returns {@code true} if a function with the specified {@code name} is registered.
     */
    boolean contains(String functionName);
}
