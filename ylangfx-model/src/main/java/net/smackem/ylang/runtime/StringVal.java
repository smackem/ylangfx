package net.smackem.ylang.runtime;

import java.util.Objects;

public class StringVal extends Value {
    private final String value;

    public static final StringVal EMPTY = new StringVal("");

    public StringVal(String value) {
        super(ValueType.STRING);
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public boolean isEmpty() {
        return this.value == null || this.value.length() == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final StringVal stringVal = (StringVal) o;
        return Objects.equals(value, stringVal.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "StringVal{" +
               "value='" + value + '\'' +
               '}';
    }
}
