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
    MAP(9),
    IMAGE(10),
    BOOLEAN(11),
    STRING(12),
    NIL(13);

    public static final int MAX_INDEX = 14;

    private final int index;

    ValueType(int index) {
        this.index = index;
    }

    public int index() {
        return this.index;
    }
}
