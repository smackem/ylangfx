package net.smackem.ylang.lang;

import net.smackem.ylang.runtime.Value;

import java.util.Objects;

public final class Instruction {
    private final OpCode opCode;
    private Value valueArg;
    private int intArg;
    private String strArg;

    public Instruction(OpCode opCode) {
        this.opCode = opCode;
    }

    public Instruction(OpCode opCode, Value valueArg) {
        this.opCode = opCode;
        this.valueArg = valueArg;
    }

    public Instruction(OpCode opCode, int intArg) {
        this.opCode = opCode;
        this.intArg = intArg;
    }

    public Instruction(OpCode opCode, String strArg) {
        this.opCode = opCode;
        this.strArg = strArg;
    }

    public OpCode opCode() {
        return this.opCode;
    }

    public Value valueArg() {
        return this.valueArg;
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
