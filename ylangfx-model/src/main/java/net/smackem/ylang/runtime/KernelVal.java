package net.smackem.ylang.runtime;

import java.util.*;
import java.util.stream.Collectors;

public class KernelVal extends Value implements Iterable<Value> {
    private final int width;
    private final int height;
    private final NumberVal[] values;

    private KernelVal(int width, int height, NumberVal[] values) {
        super(ValueType.KERNEL);
        this.width = width;
        this.height = height;
        this.values = values;
    }

    private KernelVal(int len, NumberVal[] values) {
        this(len, len, values);
    }

    public KernelVal(int width, int height, float initialValue) {
        this(width, height, create(width, height, new NumberVal(initialValue)));
    }

    public KernelVal(Collection<NumberVal> values) {
        this(quadraticLen(values), values.toArray(new NumberVal[0]));
    }

    @SuppressWarnings("CopyConstructorMissesField")
    public KernelVal(KernelVal original) {
        this(original.width, original.height, Arrays.copyOf(original.values, original.values.length));
    }

    public static KernelVal laplace(int radius) {
        final int len = radius * 2 + 1;
        final KernelVal kernel = new KernelVal(len, len, -1);
        kernel.values[kernel.values.length / 2] = new NumberVal(kernel.values.length - 1);
        return kernel;
    }

    // formula: e^(-sqrt(r) * d^2 / r^2), where d is the distance to the kernel center and r is the kernel radius
    public static KernelVal gauss(int radius) {
        final int len = radius * 2 + 1;
        final double r2 = radius * radius;
        final KernelVal kernel = new KernelVal(len, len, 0);
        int index = 0;
        for (int y = 0; y < len; y++) {
            for (int x = 0; x < len; x++) {
                final double d = Math.hypot(radius - x, radius - y); // distance to center
                final double v = Math.exp(-Math.sqrt(radius) * d * d / r2);
                kernel.values[index] = new NumberVal((float) v);
                index++;
            }
        }
        return kernel;
    }

    public NumberVal get(int x, int y) {
        Objects.checkIndex(x, this.width);
        Objects.checkIndex(y, this.height);
        return this.values[y * this.width + x];
    }

    public NumberVal get(int index) {
        return this.values[index];
    }

    public void set(int index, NumberVal n) {
        this.values[index] = n;
    }

    public int width() {
        return this.width;
    }

    public int height() {
        return this.height;
    }

    public int size() {
        return this.values.length;
    }

    public boolean contains(NumberVal n) {
        for (final NumberVal v : this.values) {
            if (v.equals(n)) {
                return true;
            }
        }
        return false;
    }

    public NumberVal sum() {
        float result = 0;
        for (final NumberVal v : this.values) {
            result += v.value();
        }
        return new NumberVal(result);
    }

    /**
     * @return the minimum kernel element or {@code null} if kernel is empty
     */
    public NumberVal min() {
        return Arrays.stream(this.values)
                .min(Comparator.comparing(NumberVal::value))
                .orElse(null);
    }

    /**
     * @return the maximum kernel element or {@code null} if kernel is empty
     */
    public NumberVal max() {
        return Arrays.stream(this.values)
                .max(Comparator.comparing(NumberVal::value))
                .orElse(null);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Iterator<Value> iterator() {
        return Arrays.stream(this.values).map(v -> (Value)v).iterator();
    }

    private static NumberVal[] create(int width, int height, NumberVal n) {
        if (width <= 0) {
            throw new IllegalArgumentException("width must be positive");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("height must be positive");
        }
        Objects.requireNonNull(n);
        final NumberVal[] values = new NumberVal[width * height];
        Arrays.fill(values, n);
        return values;
    }

    private static int quadraticLen(Collection<NumberVal> values) {
        final double sqrt = Math.sqrt(values.size());
        final int len = (int)sqrt;
        if (sqrt - len != 0.0) {
            throw new IllegalArgumentException("the kernel size must be quadratic!");
        }
        return len;
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
               Arrays.equals(values, values1.values);
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
               ", values=" + Arrays.toString(values) +
               '}';
    }

    @Override
    public String toLangString() {
        return Arrays.stream(this.values)
                .map(NumberVal::toLangString)
                .collect(Collectors.joining(" ", "|", "|"));
    }
}
