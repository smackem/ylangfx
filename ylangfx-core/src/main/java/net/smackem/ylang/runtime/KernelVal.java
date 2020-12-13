package net.smackem.ylang.runtime;

import net.smackem.ylang.interop.MatrixComposition;
import net.smackem.ylang.interop.Yln;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class KernelVal extends MatrixVal<NumberVal> implements Iterable<Value> {
    private final NumberVal[] values;
    private final PixelBufferOperations bufferOps;

    private KernelVal(int width, int height, NumberVal[] values) {
        super(ValueType.KERNEL, width, height);
        this.values = values;
        this.bufferOps = getBufferOps();
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
        this.bufferOps = getBufferOps();
    }

    public static KernelVal laplacian(int radius) {
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

    public KernelVal convolve(KernelVal kernel) {
        return this.bufferOps.convolve(this, kernel);
    }

    public KernelVal add(KernelVal right) {
        return this.bufferOps.add(this, right);
    }

    public KernelVal subtract(KernelVal right) {
        return this.bufferOps.subtract(this, right);
    }

    public KernelVal multiply(KernelVal right) {
        return this.bufferOps.multiply(this, right);
    }

    public KernelVal divide(KernelVal right) {
        return this.bufferOps.divide(this, right);
    }

    public KernelVal modulo(KernelVal right) {
        return this.bufferOps.modulo(this, right);
    }

    public KernelVal hypot(KernelVal right) {
        return this.bufferOps.hypot(this, right);
    }

    public static KernelVal min(KernelVal a, KernelVal b) {
        return Objects.requireNonNull(a).bufferOps.min(a, b);
    }

    public static KernelVal max(KernelVal a, KernelVal b) {
        return Objects.requireNonNull(a).bufferOps.max(a, b);
    }

    float[] floatValues() {
        float[] values = new float[this.values.length];
        for (int i = 0; i < values.length; i++) {
            values[i] = this.values[i].value();
        }
        return values;
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

    private static PixelBufferOperations getBufferOps() {
        return Yln.INSTANCE != null
                ? new NativePixelBufferOperations(Yln.INSTANCE)
                : new JavaPixelBufferOperations();
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
        return this.values.length < 1000
                ? Arrays.stream(this.values)
                    .map(NumberVal::toLangString)
                    .collect(Collectors.joining(" ", "|", "|"))
                : "kernel{size=%dx%d, hash=%x}".formatted(width(), height(), hashCode());
    }

    private interface PixelBufferOperations {
        KernelVal convolve(KernelVal image, KernelVal kernel);
        KernelVal add(KernelVal left, KernelVal right);
        KernelVal subtract(KernelVal left, KernelVal right);
        KernelVal multiply(KernelVal left, KernelVal right);
        KernelVal divide(KernelVal left, KernelVal right);
        KernelVal modulo(KernelVal left, KernelVal right);
        KernelVal hypot(KernelVal left, KernelVal right);
        KernelVal min(KernelVal a, KernelVal b);
        KernelVal max(KernelVal a, KernelVal b);
    }

    private static class JavaPixelBufferOperations implements PixelBufferOperations {
        @Override
        @SuppressWarnings("DuplicatedCode")
        public KernelVal convolve(KernelVal image, KernelVal kernel) {
            final int width = image.width();
            final int height = image.height();
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
                                final NumberVal px = image.values[imageIndex];
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

        @Override
        public KernelVal add(KernelVal left, KernelVal right) {
            return compose(left, right, (a, b) -> new NumberVal(a.value() + b.value()));
        }

        @Override
        public KernelVal subtract(KernelVal left, KernelVal right) {
            return compose(left, right, (a, b) -> new NumberVal(a.value() - b.value()));
        }

        @Override
        public KernelVal multiply(KernelVal left, KernelVal right) {
            return compose(left, right, (a, b) -> new NumberVal(a.value() * b.value()));
        }

        @Override
        public KernelVal divide(KernelVal left, KernelVal right) {
            return compose(left, right, (a, b) -> new NumberVal(a.value() / b.value()));
        }

        @Override
        public KernelVal modulo(KernelVal left, KernelVal right) {
            return compose(left, right, (a, b) -> new NumberVal(a.value() % b.value()));
        }

        @Override
        public KernelVal hypot(KernelVal left, KernelVal right) {
            return compose(left, right, (a, b) -> new NumberVal((float) Math.hypot(a.value(), b.value())));
        }

        @Override
        public KernelVal min(KernelVal left, KernelVal right) {
            return compose(left, right, NumberVal::min);
        }

        @Override
        public KernelVal max(KernelVal left, KernelVal right) {
            return compose(left, right, NumberVal::max);
        }

        private KernelVal compose(KernelVal left, KernelVal right, BiFunction<NumberVal, NumberVal, NumberVal> operation) {
            if (Objects.requireNonNull(right).width() != left.width() || right.height() != left.height()) {
                throw new IllegalArgumentException("composed kernels must have the same dimensions");
            }
            final KernelVal result = new KernelVal(left.width(), left.height(), 0);
            for (int i = 0; i < left.values.length; i++) {
                result.values[i] = operation.apply(left.values[i], right.values[i]);
            }
            return result;
        }
    }

    private static class NativePixelBufferOperations implements PixelBufferOperations {
        private final Yln yln;

        NativePixelBufferOperations(Yln yln) {
            this.yln = yln;
        }

        private static KernelVal fromFloatValues(float[] values, int width, int height) {
            final NumberVal[] numberValues = new NumberVal[values.length];
            for (int i = 0; i < numberValues.length; i++) {
                numberValues[i] = new NumberVal(values[i]);
            }
            return new KernelVal(width, height, numberValues);
        }

        @Override
        public KernelVal convolve(KernelVal image, KernelVal kernel) {
            return fromFloatValues(
                    yln.convolveKernel(image.width(), image.height(), image.floatValues(),
                            kernel.width(), kernel.height(), kernel.floatValues()),
                    image.width(), image.height());
        }

        @Override
        public KernelVal add(KernelVal left, KernelVal right) {
            return fromFloatValues(
                    yln.composeKernels(left.width(), left.height(),left.floatValues(),
                            right.floatValues(), MatrixComposition.ADD),
                    left.width(), left.height());
        }

        @Override
        public KernelVal subtract(KernelVal left, KernelVal right) {
            return fromFloatValues(
                    yln.composeKernels(left.width(), left.height(),left.floatValues(),
                            right.floatValues(), MatrixComposition.SUB),
                    left.width(), left.height());
        }

        @Override
        public KernelVal multiply(KernelVal left, KernelVal right) {
            return fromFloatValues(
                    yln.composeKernels(left.width(), left.height(),left.floatValues(),
                            right.floatValues(), MatrixComposition.MUL),
                    left.width(), left.height());
        }

        @Override
        public KernelVal divide(KernelVal left, KernelVal right) {
            return fromFloatValues(
                    yln.composeKernels(left.width(), left.height(),left.floatValues(),
                            right.floatValues(), MatrixComposition.DIV),
                    left.width(), left.height());
        }

        @Override
        public KernelVal modulo(KernelVal left, KernelVal right) {
            return fromFloatValues(
                    yln.composeKernels(left.width(), left.height(),left.floatValues(),
                            right.floatValues(), MatrixComposition.MOD),
                    left.width(), left.height());
        }

        @Override
        public KernelVal hypot(KernelVal left, KernelVal right) {
            return fromFloatValues(
                    yln.composeKernels(left.width(), left.height(),left.floatValues(),
                            right.floatValues(), MatrixComposition.HYPOT),
                    left.width(), left.height());
        }

        @Override
        public KernelVal min(KernelVal a, KernelVal b) {
            return fromFloatValues(
                    yln.composeKernels(a.width(), a.height(),a.floatValues(),
                            b.floatValues(), MatrixComposition.MIN),
                    a.width(), a.height());
        }

        @Override
        public KernelVal max(KernelVal a, KernelVal b) {
            return fromFloatValues(
                    yln.composeKernels(a.width(), a.height(),a.floatValues(),
                            b.floatValues(), MatrixComposition.MAX),
                    a.width(), a.height());
        }
    }
}
