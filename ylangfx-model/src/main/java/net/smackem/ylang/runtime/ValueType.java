package net.smackem.ylang.runtime;

public enum ValueType {
    NUMBER(0),
    POINT(1),
    RGB(2),
    HSV(3),
    RECT(4),
    CIRCLE(5),
    POLYGON(6),
    KERNEL(7),
    LIST(8),
    MAP(9);

    public static final int MAX_INDEX = 10;

    private final int index;

    ValueType(int index) {
        this.index = index;
    }

    public int index() {
        return this.index;
    }
}
