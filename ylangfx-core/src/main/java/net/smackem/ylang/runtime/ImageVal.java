package net.smackem.ylang.runtime;

import net.smackem.ylang.interop.MatrixComposition;
import net.smackem.ylang.interop.Yln;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;

@SuppressWarnings("DuplicatedCode")
public class ImageVal extends MatrixVal<RgbVal> {
    private final RgbVal[] pixels;
    private final PixelBufferOperations bufferOps;

    private ImageVal(int width, int height, RgbVal[] pixels) {
        super(ValueType.IMAGE, width, height);
        if (Objects.requireNonNull(pixels).length != width * height) {
            throw new IllegalArgumentException("pixel buffer size does not match width and height");
        }
        this.pixels = pixels;
        this.bufferOps = getBufferOps();
    }

    public ImageVal(int width, int height) {
        this(width, height, getPixels(width, height, RgbVal.EMPTY));
    }

    public ImageVal(int width, int height, RgbVal initialValue) {
        this(width, height, getPixels(width, height, initialValue));
    }

    public ImageVal(ImageVal original) {
        super(original);
        this.pixels = clonePixels(original.pixels);
        this.bufferOps = getBufferOps();
    }

    public static ImageVal fromArgbPixels(int width, int height, int[] pixels) {
        if (width <= 0) {
            throw new IllegalArgumentException("image width must be > 0");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("image height must be > 0");
        }
        if (width * height != pixels.length) {
            throw new IllegalArgumentException("width and height do not match number of pixels");
        }
        final RgbVal[] rgbPixels = new RgbVal[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            final int pixel = pixels[i];
            rgbPixels[i] = new RgbVal(pixel >> 16 & 0xff, pixel >> 8 & 0xff, pixel & 0xff, pixel >> 24 & 0xff);
        }
        return new ImageVal(width, height, rgbPixels);
    }

    public static ImageVal fromKernel(KernelVal kernel) {
        Objects.requireNonNull(kernel);
        final ImageVal image = new ImageVal(kernel.width(), kernel.height());
        for (int i = 0; i < image.pixels.length; i++) {
            final float value = RgbVal.clamp(kernel.get(i).value());
            image.pixels[i] = new RgbVal(value, value, value, 255);
        }
        return image;
    }

    public int[] toArgbPixels() {
        final int[] buffer = new int[width() * height()];
        final int pixelCount = buffer.length;
        for (int i = 0; i < pixelCount; i++) {
            buffer[i] = toIntArgb(this.pixels[i]);
        }
        return buffer;
    }

    public RgbVal convolve(int x, int y, KernelVal kernel) {
        final int width = width();
        final int height = height();
        final int kernelWidth = kernel.width();
        final int kernelHeight = kernel.height();
        final int halfKernelWidth = kernelWidth / 2;
        final int halfKernelHeight = kernelHeight / 2;
        float kernelSum = 0;
        float r = 0;
        float g = 0;
        float b = 0;
        float a = 255;
        int kernelIndex = 0;

        for (int kernelY = 0; kernelY < kernelHeight; kernelY++) {
            for (int kernelX = 0; kernelX < kernelWidth; kernelX++) {
                final int sourceY = y - halfKernelHeight + kernelY;
                final int sourceX = x - halfKernelWidth + kernelX;
                if (sourceX >= 0 && sourceX < width && sourceY >= 0 && sourceY < height) {
                    final float value = kernel.get(kernelIndex).value();
                    final RgbVal px = get(sourceX, sourceY);
                    r += value * px.r();
                    g += value * px.g();
                    b += value * px.b();
                    kernelSum += value;

                    if (sourceX == x && sourceY == y) {
                        a = px.a();
                    }
                }
                kernelIndex++;
            }
        }
        if (kernelSum == 0) {
            return new RgbVal(r, g, b, a);
        }

        return new RgbVal(r / kernelSum, g / kernelSum, b / kernelSum, a);
    }

    public ImageVal invert() {
        final ImageVal result = new ImageVal(width(), height());
        for (int i = 0; i < result.pixels.length; i++) {
            result.pixels[i] = this.pixels[i].invert();
        }
        return result;
    }

    public ImageVal convolve(KernelVal kernel) {
        return this.bufferOps.convolve(this, kernel);
    }

    public ImageVal add(ImageVal right) {
        return this.bufferOps.add(this, right);
    }

    public ImageVal subtract(ImageVal right) {
        return this.bufferOps.subtract(this, right);
    }

    public ImageVal multiply(ImageVal right) {
        return this.bufferOps.multiply(this, right);
    }

    public ImageVal divide(ImageVal right) {
        return this.bufferOps.divide(this, right);
    }

    public ImageVal over(ImageVal background) {
        return this.bufferOps.over(this, background);
    }

    public ImageVal hypot(ImageVal right) {
        return this.bufferOps.hypot(this, right);
    }

    public static ImageVal min(ImageVal a, ImageVal b) {
        return Objects.requireNonNull(a).bufferOps.min(a, b);
    }

    public static ImageVal max(ImageVal a, ImageVal b) {
        return Objects.requireNonNull(a).bufferOps.max(a, b);
    }

    @Override
    public String toString() {
        return "image{size=%dx%d, hash=%x}".formatted(width(), height(), hashCode());
    }

    @Override
    RgbVal internalGet(int index) {
        return this.pixels[index];
    }

    @Override
    void internalSet(int index, RgbVal value) {
        this.pixels[index] = value;
    }

    private static RgbVal[] getPixels(int width, int height, RgbVal initialValue) {
        if (width <= 0) {
            throw new IllegalArgumentException("image width must be > 0");
        }
        if (height <= 0) {
            throw new IllegalArgumentException("image height must be > 0");
        }
        Objects.requireNonNull(initialValue);
        final RgbVal[] pixels = new RgbVal[width * height];
        Arrays.fill(pixels, initialValue);
        return pixels;
    }

    private static RgbVal[] clonePixels(RgbVal[] pixels) {
        final RgbVal[] clone = new RgbVal[pixels.length];
        System.arraycopy(pixels, 0, clone, 0, pixels.length);
        return clone;
    }

    private static PixelBufferOperations getBufferOps() {
        return Yln.INSTANCE != null
                ? new NativePixelBufferOperations(Yln.INSTANCE)
                : new JavaPixelBufferOperations();
    }

    private static int toIntArgb(RgbVal rgb) {
        return (int) RgbVal.clamp(rgb.a()) << 24 |
               (int) RgbVal.clamp(rgb.r()) << 16 |
               (int) RgbVal.clamp(rgb.g()) << 8 |
               (int) RgbVal.clamp(rgb.b());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ImageVal imageVal = (ImageVal) o;
        return Arrays.equals(pixels, imageVal.pixels);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(pixels);
    }

    private interface PixelBufferOperations {
        ImageVal convolve(ImageVal image, KernelVal kernel);
        ImageVal add(ImageVal left, ImageVal right);
        ImageVal subtract(ImageVal left, ImageVal right);
        ImageVal multiply(ImageVal left, ImageVal right);
        ImageVal divide(ImageVal left, ImageVal right);
        ImageVal over(ImageVal left, ImageVal background);
        ImageVal hypot(ImageVal left, ImageVal right);
        ImageVal min(ImageVal a, ImageVal b);
        ImageVal max(ImageVal a, ImageVal b);
    }

    private static class JavaPixelBufferOperations implements PixelBufferOperations {
        @Override
        public ImageVal convolve(ImageVal image, KernelVal kernel) {
            final int width = image.width();
            final int height = image.height();
            final ImageVal target = new ImageVal(width, height);
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
                    float r = 0;
                    float g = 0;
                    float b = 0;
                    float a = 255;
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
                                final RgbVal px = image.pixels[imageIndex];
                                r += value * px.r();
                                g += value * px.g();
                                b += value * px.b();
                                if (imageX == x && imageY == y) {
                                    a = px.a();
                                }
                            }
                            kernelIndex++;
                            imageIndex++;
                        }
                    }
                    target.pixels[targetIndex] = kernelSum == 0
                            ? new RgbVal(r, g, b, a)
                            : new RgbVal(r / kernelSum, g / kernelSum, b / kernelSum, a);
                    targetIndex++;
                }
            }
            return target;
        }

        @Override
        public ImageVal add(ImageVal left, ImageVal right) {
            return compose(left, right, RgbVal::add);
        }

        @Override
        public ImageVal subtract(ImageVal left, ImageVal right) {
            return compose(left, right, RgbVal::subtract);
        }

        @Override
        public ImageVal multiply(ImageVal left, ImageVal right) {
            return compose(left, right, RgbVal::multiplyWith);
        }

        @Override
        public ImageVal divide(ImageVal left, ImageVal right) {
            return compose(left, right, RgbVal::divideBy);
        }

        @Override
        public ImageVal over(ImageVal left, ImageVal right) {
            return compose(left, right, RgbVal::over);
        }

        @Override
        public ImageVal hypot(ImageVal left, ImageVal right) {
            return compose(left, right, RgbVal::hypot);
        }

        @Override
        public ImageVal min(ImageVal a, ImageVal b) {
            return compose(a, b, RgbVal::min);
        }

        @Override
        public ImageVal max(ImageVal a, ImageVal b) {
            return compose(a, b, RgbVal::max);
        }

        private ImageVal compose(ImageVal left, ImageVal right, BiFunction<RgbVal, RgbVal, RgbVal> operation) {
            Objects.requireNonNull(left);
            Objects.requireNonNull(right);
            Objects.requireNonNull(operation);
            if (left.width() != right.width() || left.height() != right.height()) {
                throw new IllegalArgumentException("left and right must have equal dimensions");
            }
            final ImageVal result = new ImageVal(left.width(), left.height());
            for (int i = 0; i < left.pixels.length; i++) {
                result.pixels[i] = operation.apply(left.pixels[i], right.pixels[i]);
            }
            return result;
        }
    }

    private static class NativePixelBufferOperations implements PixelBufferOperations {
        private final Yln yln;

        NativePixelBufferOperations(Yln yln) {
            this.yln = Objects.requireNonNull(yln);
        }

        @Override
        public ImageVal convolve(ImageVal image, KernelVal kernel) {
            return ImageVal.fromArgbPixels(image.width(), image.height(),
                    this.yln.convolveImage(image.width(), image.height(),
                            image.toArgbPixels(), kernel.width(), kernel.height(), kernel.floatValues()));
        }

        @Override
        public ImageVal add(ImageVal left, ImageVal right) {
            return ImageVal.fromArgbPixels(left.width(), left.height(),
                    this.yln.composeImages(left.width(), left.height(),
                            left.toArgbPixels(), right.toArgbPixels(), MatrixComposition.ADD));
        }

        @Override
        public ImageVal subtract(ImageVal left, ImageVal right) {
            return ImageVal.fromArgbPixels(left.width(), left.height(),
                    this.yln.composeImages(left.width(), left.height(),
                            left.toArgbPixels(), right.toArgbPixels(), MatrixComposition.SUB));
        }

        @Override
        public ImageVal multiply(ImageVal left, ImageVal right) {
            return ImageVal.fromArgbPixels(left.width(), left.height(),
                    this.yln.composeImages(left.width(), left.height(),
                            left.toArgbPixels(), right.toArgbPixels(), MatrixComposition.MUL));
        }

        @Override
        public ImageVal divide(ImageVal left, ImageVal right) {
            return ImageVal.fromArgbPixels(left.width(), left.height(),
                    this.yln.composeImages(left.width(), left.height(),
                            left.toArgbPixels(), right.toArgbPixels(), MatrixComposition.DIV));
        }

        @Override
        public ImageVal over(ImageVal left, ImageVal right) {
            return ImageVal.fromArgbPixels(left.width(), left.height(),
                    this.yln.composeImages(left.width(), left.height(),
                            left.toArgbPixels(), right.toArgbPixels(), MatrixComposition.OVER));
        }

        @Override
        public ImageVal hypot(ImageVal left, ImageVal right) {
            return ImageVal.fromArgbPixels(left.width(), left.height(),
                    this.yln.composeImages(left.width(), left.height(),
                            left.toArgbPixels(), right.toArgbPixels(), MatrixComposition.HYPOT));
        }

        @Override
        public ImageVal min(ImageVal a, ImageVal b) {
            return ImageVal.fromArgbPixels(a.width(), a.height(),
                    this.yln.composeImages(a.width(), a.height(),
                            a.toArgbPixels(), b.toArgbPixels(), MatrixComposition.MIN));
        }

        @Override
        public ImageVal max(ImageVal a, ImageVal b) {
            return ImageVal.fromArgbPixels(a.width(), a.height(),
                    this.yln.composeImages(a.width(), a.height(),
                            a.toArgbPixels(), b.toArgbPixels(), MatrixComposition.MAX));
        }
    }
}
