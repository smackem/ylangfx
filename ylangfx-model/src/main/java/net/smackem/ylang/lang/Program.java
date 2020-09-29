package net.smackem.ylang.lang;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A compiled program.
 */
public final class Program {
    private final List<Instruction> instructions;

    /**
     * Initializes a new instance of {@link Program}.
     * @param instructions The emitted instructions.
     */
    public Program(List<Instruction> instructions) {
        if (Objects.requireNonNull(instructions).size() == 0) {
            throw new IllegalArgumentException("there must be at least one instruction");
        }
        this.instructions = Collections.unmodifiableList(instructions);
    }

    /**
     * @return The emitted instructions that make up the program.
     */
    public List<Instruction> instructions() {
        return this.instructions;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        for (final Instruction instr : this.instructions) {
            sb.append(instr.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Program program = (Program) o;

        return instructions.equals(program.instructions);
    }

    @Override
    public int hashCode() {
        return instructions.hashCode();
    }

    public static class ParseException extends Exception {
        private ParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
