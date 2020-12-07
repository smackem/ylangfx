package net.smackem.ylang.interop;

public enum MatrixComposition {
    ADD(1),
    SUB(2),
    MUL(3),
    DIV(4),
    MOD(5),
    HYPOT(6),
    OVER(7),
    MIN(8),
    MAX(9);

    private final int nativeValue;

    MatrixComposition(int nativeValue) {
        this.nativeValue = nativeValue;
    }

    public int nativeValue() {
        return this.nativeValue;
    }
}
