package net.smackem.ylang.runtime;

import java.util.Objects;

public abstract class MatrixVal<T extends Value> extends Value {
    private final int width;
    private final int height;
    private IntRect clipRect;
    private T defaultElement;

    MatrixVal(ValueType valueType, int width, int height) {
        super(valueType);
        this.width = width;
        this.height = height;
    }

    MatrixVal(MatrixVal<T> other) {
        this(other.type(), other.width, other.height);
        this.clipRect = other.clipRect;
        this.defaultElement = other.defaultElement;
    }

    public final int width() {
        return this.width;
    }

    public final int height() {
        return this.height;
    }

    public final IntRect getClipRect() {
        return this.clipRect;
    }

    public final void setClipRect(IntRect value) {
        this.clipRect = value;
    }

    /**
     * @return the value to return in {{@link #get(int, int)}} for coordinates that are out of bounds.
     */
    public final T getDefaultElement() {
        return this.defaultElement;
    }

    /**
     * sets the value to return in {{@link #get(int, int)}} for coordinates that are out of bounds.
     */
    public final void setDefaultElement(T value) {
        this.defaultElement = value;
    }

    public final T get(int x, int y) {
        if (y < 0 || y >= this.height || x < 0 || x >= this.width) {
            if (this.defaultElement != null) {
                return this.defaultElement;
            }
            throw new IndexOutOfBoundsException("coordinates out of range " + x + "," + y);
        }
        return internalGet(y * this.width + x);
    }

    public final void set(int x, int y, T value) {
        if (this.clipRect != null && this.clipRect.contains(x, y) == false) {
            return;
        }
        if (y < 0 || y >= this.height || x < 0 || x >= this.width) {
            throw new IndexOutOfBoundsException("coordinates out of range " + x + "," + y);
        }
        internalSet(y * this.width + x, value);
    }

    public final KernelVal selectKernel(int x, int y, KernelVal kernel, ToFloatFunction<T> selector) {
        final int kernelWidth = kernel.width();
        final int kernelHeight = kernel.height();
        final KernelVal result = new KernelVal(kernelWidth, kernelHeight, 0);
        int kernelIndex = 0;

        for (int kernelY = 0; kernelY < kernelHeight; kernelY++) {
            for (int kernelX = 0; kernelX < kernelWidth; kernelX++) {
                final int sourceY = y - (kernelHeight / 2) + kernelY;
                final int sourceX = x - (kernelWidth / 2) + kernelX;
                if (sourceX >= 0 && sourceX < this.width && sourceY >= 0 && sourceY < this.height) {
                    final float value = kernel.get(kernelIndex).value();
                    final T v = get(sourceX, sourceY);
                    final float f = selector.apply(v);
                    result.set(kernelIndex, new NumberVal(value * f));
                }
                kernelIndex++;
            }
        }

        return result;
    }

    public final void plot(GeometryVal geometry, T value) {
        for (final Value v : geometry) {
            final PointVal pt = (PointVal) v;
            set((int) pt.x(), (int) pt.y(), value);
        }
    }

    abstract T internalGet(int index);

    abstract void internalSet(int index, T value);

    @Override
    public String toString() {
        return "MatrixVal{size=%dx%d, hash=%x}".formatted(this.width, this.height, hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final MatrixVal<?> matrixVal = (MatrixVal<?>) o;
        return width == matrixVal.width &&
               height == matrixVal.height;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }
}
