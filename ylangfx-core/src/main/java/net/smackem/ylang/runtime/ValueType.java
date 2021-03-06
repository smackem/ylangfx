package net.smackem.ylang.runtime;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

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
    NIL(13),
    RANGE(14),
    LINE(15),
    FUNCTION(16),
    MAP_ENTRY(17),
    INFRASTRUCTURE(-1);

    public static final int MAX_INDEX = 18; // array size to hold all indices

    public static Collection<ValueType> publicValues() {
        return Arrays.stream(values())
                .filter(valueType -> valueType.index >= 0)
                .collect(Collectors.toList());
    }

    public boolean isGeometry() {
        return switch (this) {
            case POINT, RECT, CIRCLE, LINE, POLYGON -> true;
            default -> false;
        };
    }

    public boolean isMatrix() {
        return switch (this) {
            case IMAGE, KERNEL -> true;
            default -> false;
        };
    }

    public boolean isIterable() {
        return switch (this) {
            case LIST, RANGE, KERNEL -> true;
            default -> isGeometry();
        };
    }

    private final int index;

    ValueType(int index) {
        this.index = index;
    }

    public int index() {
        return this.index;
    }
}
