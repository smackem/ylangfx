package net.smackem.ylang.lang;

import net.smackem.ylang.runtime.Value;

import java.util.Objects;

public final class Instruction {
    private final OpCode opCode;
    private final DebugInfo debugInfo;
    private Value valueArg;
    private int intArg;
    private String strArg;

    public Instruction(DebugInfo debugInfo, OpCode opCode) {
        this.debugInfo = debugInfo;
        this.opCode = opCode;
    }

    public Instruction(DebugInfo debugInfo, OpCode opCode, Value valueArg) {
        this.debugInfo = debugInfo;
        this.opCode = opCode;
        this.valueArg = valueArg;
    }

    public Instruction(DebugInfo debugInfo, OpCode opCode, int intArg) {
        this.debugInfo = debugInfo;
        this.opCode = opCode;
        this.intArg = intArg;
    }

    public Instruction(DebugInfo debugInfo, OpCode opCode, String strArg) {
        this.debugInfo = debugInfo;
        this.opCode = opCode;
        this.strArg = strArg;
    }

    public Instruction(DebugInfo debugInfo, OpCode opCode, int intArg, String strArg) {
        this.debugInfo = debugInfo;
        this.opCode = opCode;
        this.strArg = strArg;
        this.intArg = intArg;
    }

    public OpCode opCode() {
        return this.opCode;
    }

    public DebugInfo debugInfo() {
        return this.debugInfo;
    }

    public Value valueArg() {
        return this.valueArg;
    }

    public void setValueArg(Value value) {
        this.valueArg = value;
    }

    public int intArg() {
        return this.intArg;
    }

    public void setIntArg(int value) {
        this.intArg = value;
    }

    public String strArg() {
        return this.strArg;
    }

    public void setStrArg(String value) {
        this.strArg = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Instruction that = (Instruction) o;
        return intArg == that.intArg &&
               opCode == that.opCode &&
               Objects.equals(valueArg, that.valueArg) &&
               Objects.equals(strArg, that.strArg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(opCode, valueArg, intArg, strArg);
    }

    @Override
    public String toString() {
        return "Instruction{" +
               "opCode=" + opCode +
               ", valueArg=" + valueArg +
               ", intArg=" + intArg +
               ", strArg='" + strArg + '\'' +
               '}';
    }
}
