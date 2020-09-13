package net.smackem.ylang.runtime;

import java.util.*;

public class KernelVal extends Value implements Iterable<Value> {
    private final int width;
    private final int height;
    private final List<NumberVal> values;

    public KernelVal(int width, int height, float initialValue) {
        this(create(width, height, new NumberVal(initialValue)));
    }

    public KernelVal(Collection<NumberVal> values) {
        super(ValueType.KERNEL);
        final double sqrt = Math.sqrt(values.size());
        final int len = (int)sqrt;
        if (sqrt - len != 0.0) {
            throw new IllegalArgumentException("the kernel size must be quadratic!");
        }
        this.width = len;
        this.height = len;
        this.values = List.copyOf(values);
    }

    public NumberVal get(int x, int y) {
        return this.values.get(y * this.width + x);
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }

    public boolean contains(NumberVal n) {
        return this.values.contains(n);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Iterator<Value> iterator() {
        return this.values.stream().map(v -> (Value)v).iterator();
    }

    private static Collection<NumberVal> create(int width, int height, NumberVal n) {
        if (width <= 0) {
            throw new IllegalArgumentException("width must be positive");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("height must be positive");
        }
        return Collections.nCopies(width * height, Objects.requireNonNull(n));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final KernelVal values1 = (KernelVal) o;
        return width == values1.width &&
               height == values1.height &&
               Objects.equals(values, values1.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height, values);
    }

    @Override
    public String toString() {
        return "KernelVal{" +
               "width=" + width +
               ", height=" + height +
               ", values=" + values +
               '}';
    }
}
