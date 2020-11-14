package net.smackem.ylang.runtime;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class KernelVal extends MatrixVal<NumberVal> implements Iterable<Value> {
    private final NumberVal[] values;

    private KernelVal(int width, int height, NumberVal[] values) {
        super(ValueType.KERNEL, width, height);
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

    public KernelVal(KernelVal original) {
        super(original);
        this.values = Arrays.copyOf(original.values, original.values.length);
    }

    public static KernelVal laplace(int radius) {
        final int len = radius * 2 + 1;
        final KernelVal kernel = new KernelVal(len, len, -1);
        kernel.values[kernel.values.length / 2] = new NumberVal(kernel.values.length - 1);
        return kernel;
    }

    // formula: e^(-sqrt(r) * d^2 / r^2), where d is the distance to the kernel center and r is the kernel radius
    public static KernelVal gaussian(int radius) {
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

    public static KernelVal fromImage(ImageVal image) {
        final KernelVal kernel = new KernelVal(image.width(), image.height(), 0);
        for (int i = 0; i < kernel.values.length; i++) {
            kernel.values[i] = new NumberVal(image.internalGet(i).intensity());
        }
        return kernel;
    }

    public void sort() {
        Arrays.sort(this.values, Comparator.comparing(NumberVal::value));
    }

    public NumberVal get(int index) {
        return internalGet(index);
    }

    public void set(int index, NumberVal v) {
        internalSet(index, v);
    }

    @Override
    NumberVal internalGet(int index) {
        return this.values[index];
    }

    @Override
    void internalSet(int index, NumberVal value) {
        this.values[index] = value;
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

    @SuppressWarnings("DuplicatedCode")
    public KernelVal convolve(KernelVal kernel) {
        final int width = width();
        final int height = height();
        final KernelVal target = new KernelVal(width, height, 0);
        final float kernelSum = kernel.sum().value();
        final int kernelWidth = kernel.width();
        final int kernelHeight = kernel.height();
        final int halfKernelWidth = kernelWidth / 2;
        final int halfKernelHeight = kernelHeight / 2;
        final float[] kernelValues = new float[kernel.size()];
        int targetIndex = 0;
        int i = 0;
        for (final Value n : kernel) {
            kernelValues[i++] = ((NumberVal) n).value();
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float acc = 0;
                int startY = y - halfKernelHeight;
                int endY = startY + kernelHeight;
                int startX = x - halfKernelWidth;
                int endX = startX + kernelWidth;
                int kernelIndex = 0;
                for (int imageY = startY; imageY < endY; imageY++) {
                    if (imageY < 0 || imageY >= height) {
                        kernelIndex += kernelWidth;
                        continue;
                    }
                    int imageIndex = imageY * width + startX;
                    for (int imageX = startX; imageX < endX; imageX++) {
                        if (imageX >= 0 && imageX < width) {
                            final float value = kernelValues[kernelIndex];
                            final NumberVal px = this.values[imageIndex];
                            acc += value * px.value();
                        }
                        kernelIndex++;
                        imageIndex++;
                    }
                }
                target.values[targetIndex] = new NumberVal(kernelSum == 0 ? acc : acc / kernelSum);
                targetIndex++;
            }
        }
        return target;
    }

    @SuppressWarnings("DuplicatedCode")
    public float convolve(int x, int y, KernelVal kernel) {
        final int width = width();
        final int height = height();
        final int kernelWidth = kernel.width();
        final int kernelHeight = kernel.height();
        final int halfKernelWidth = kernelWidth / 2;
        final int halfKernelHeight = kernelHeight / 2;
        float kernelSum = 0;
        float acc = 0;
        int kernelIndex = 0;

        for (int kernelY = 0; kernelY < kernelHeight; kernelY++) {
            for (int kernelX = 0; kernelX < kernelWidth; kernelX++) {
                final int sourceY = y - halfKernelHeight + kernelY;
                final int sourceX = x - halfKernelWidth + kernelX;
                if (sourceX >= 0 && sourceX < width && sourceY >= 0 && sourceY < height) {
                    final float value = kernel.values[kernelIndex].value();
                    final NumberVal n = get(sourceX, sourceY);
                    acc += value * n.value();
                    kernelSum += value;
                }
                kernelIndex++;
            }
        }
        if (kernelSum == 0) {
            return acc;
        }

        return acc / kernelSum;
    }

    public KernelVal add(KernelVal right) {
        return composeWith(right, (a, b) -> new NumberVal(a.value() + b.value()));
    }

    public KernelVal subtract(KernelVal right) {
        return composeWith(right, (a, b) -> new NumberVal(a.value() - b.value()));
    }

    public KernelVal multiply(KernelVal right) {
        return composeWith(right, (a, b) -> new NumberVal(a.value() * b.value()));
    }

    public KernelVal divide(KernelVal right) {
        return composeWith(right, (a, b) -> new NumberVal(a.value() / b.value()));
    }

    public KernelVal composeWith(KernelVal that, BiFunction<NumberVal, NumberVal, NumberVal> operation) {
        if (Objects.requireNonNull(that).width() != width() || that.height() != this.height()) {
            throw new IllegalArgumentException("subtracted images must have the same dimensions");
        }
        final KernelVal result = new KernelVal(this.width(), this.height(), 0);
        for (int i = 0; i < this.values.length; i++) {
            result.values[i] = operation.apply(this.values[i], that.values[i]);
        }
        return result;
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
        return super.equals(o) &&
               Arrays.equals(values, values1.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.values);
    }

    @Override
    public String toString() {
        return "KernelVal{" +
               "width=" + width() +
               ", height=" + height() +
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
