package net.smackem.ylang.runtime;

import net.smackem.ylang.interop.MatrixComposition;
import net.smackem.ylang.interop.Yln;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiFunction;

@SuppressWarnings("DuplicatedCode")
public class ImageVal extends MatrixVal<RgbVal> {
    private static final Logger log = LoggerFactory.getLogger(ImageVal.class);
    private final RgbVal[] pixels;

    private ImageVal(int width, int height, RgbVal[] pixels) {
        super(ValueType.IMAGE, width, height);
        if (Objects.requireNonNull(pixels).length != width * height) {
            throw new IllegalArgumentException("pixel buffer size does not match width and height");
        }
        this.pixels = pixels;
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
            rgbPixels[i] = RgbVal.fromIntArgb(pixel);
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

    @Override
    public int[] toArgbPixels() {
        final int[] buffer = new int[width() * height()];
        final int pixelCount = buffer.length;
        for (int i = 0; i < pixelCount; i++) {
            buffer[i] = this.pixels[i].toIntArgb();
        }
        return buffer;
    }

    public RgbVal min() {
        float minR = Float.MAX_VALUE;
        float minG = Float.MAX_VALUE;
        float minB = Float.MAX_VALUE;
        for (final RgbVal rgb : this.pixels) {
            if (rgb.r() < minR) {
                minR = rgb.r();
            }
            if (rgb.g() < minG) {
                minG = rgb.g();
            }
            if (rgb.b() < minB) {
                minB = rgb.b();
            }
        }
        return new RgbVal(minR, minG, minB, 255);
    }

    public RgbVal max() {
        float maxR = Float.MIN_VALUE;
        float maxG = Float.MIN_VALUE;
        float maxB = Float.MIN_VALUE;
        for (final RgbVal rgb : this.pixels) {
            if (rgb.r() > maxR) {
                maxR = rgb.r();
            }
            if (rgb.g() > maxG) {
                maxG = rgb.g();
            }
            if (rgb.b() > maxB) {
                maxB = rgb.b();
            }
        }
        return new RgbVal(maxR, maxG, maxB, 255);
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
        return getBufferOps().convolve(this, kernel);
    }

    public ImageVal add(ImageVal right) {
        return getBufferOps().add(this, right);
    }

    public ImageVal subtract(ImageVal right) {
        return getBufferOps().subtract(this, right);
    }

    public ImageVal multiply(ImageVal right) {
        return getBufferOps().multiply(this, right);
    }

    public ImageVal divide(ImageVal right) {
        return getBufferOps().divide(this, right);
    }

    public ImageVal modulo(ImageVal right) {
        return getBufferOps().modulo(this, right);
    }

    public ImageVal over(ImageVal background) {
        return getBufferOps().over(this, background);
    }

    public ImageVal hypot(ImageVal right) {
        return getBufferOps().hypot(this, right);
    }

    public static ImageVal min(ImageVal a, ImageVal b) {
        return getBufferOps().min(a, b);
    }

    public static ImageVal max(ImageVal a, ImageVal b) {
        return getBufferOps().max(a, b);
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
        final Yln yln = RuntimeContext.current().yln();
        final PixelBufferOperations ops = yln != null
                ? new NativePixelBufferOperations(yln)
                : new JavaPixelBufferOperations();
        log.info("using {} for raster operations", ops.getClass().getName());
        return ops;
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
        ImageVal modulo(ImageVal left, ImageVal right);
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
        public ImageVal modulo(ImageVal left, ImageVal right) {
            return compose(left, right, RgbVal::modulo);
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

        private float[] toFloatBuffer(ImageVal image) {
            final float[] buffer = new float[image.pixels.length * 4];
            int bufferPos = 0;
            for (final RgbVal color : image.pixels) {
                buffer[bufferPos++] = color.r();
                buffer[bufferPos++] = color.g();
                buffer[bufferPos++] = color.b();
                buffer[bufferPos++] = color.a();
            }
            return buffer;
        }

        private ImageVal fromFloatBuffer(float[] buffer, int width, int height) {
            if (buffer.length < width * height * 4) {
                throw new IllegalArgumentException("float buffer too small");
            }
            final RgbVal[] pixels = new RgbVal[width * height];
            int bufferPos = 0;
            for (int i = 0; i < pixels.length; i++) {
                pixels[i] = new RgbVal(buffer[bufferPos], buffer[bufferPos + 1],
                        buffer[bufferPos + 2], buffer[bufferPos + 3]);
                bufferPos += 4;
            }
            return new ImageVal(width, height, pixels);
        }

        NativePixelBufferOperations(Yln yln) {
            this.yln = Objects.requireNonNull(yln);
        }

        @Override
        public ImageVal convolve(ImageVal image, KernelVal kernel) {
            return fromFloatBuffer(
                    this.yln.convolveImage(image.width(), image.height(), toFloatBuffer(image),
                            kernel.width(), kernel.height(), kernel.floatValues()),
                    image.width(), image.height());
        }

        @Override
        public ImageVal add(ImageVal left, ImageVal right) {
            return fromFloatBuffer(
                    this.yln.composeImages(left.width(), left.height(), toFloatBuffer(left),
                            toFloatBuffer(right), MatrixComposition.ADD),
                    left.width(), left.height());
        }

        @Override
        public ImageVal subtract(ImageVal left, ImageVal right) {
            return fromFloatBuffer(
                    this.yln.composeImages(left.width(), left.height(), toFloatBuffer(left),
                            toFloatBuffer(right), MatrixComposition.SUB),
                    left.width(), left.height());
        }

        @Override
        public ImageVal multiply(ImageVal left, ImageVal right) {
            return fromFloatBuffer(
                    this.yln.composeImages(left.width(), left.height(), toFloatBuffer(left),
                            toFloatBuffer(right), MatrixComposition.MUL),
                    left.width(), left.height());
        }

        @Override
        public ImageVal divide(ImageVal left, ImageVal right) {
            return fromFloatBuffer(
                    this.yln.composeImages(left.width(), left.height(), toFloatBuffer(left),
                            toFloatBuffer(right), MatrixComposition.DIV),
                    left.width(), left.height());
        }

        @Override
        public ImageVal modulo(ImageVal left, ImageVal right) {
            return fromFloatBuffer(
                    this.yln.composeImages(left.width(), left.height(), toFloatBuffer(left),
                            toFloatBuffer(right), MatrixComposition.MOD),
                    left.width(), left.height());
        }

        @Override
        public ImageVal over(ImageVal left, ImageVal right) {
            return fromFloatBuffer(
                    this.yln.composeImages(left.width(), left.height(), toFloatBuffer(left),
                            toFloatBuffer(right), MatrixComposition.OVER),
                    left.width(), left.height());
        }

        @Override
        public ImageVal hypot(ImageVal left, ImageVal right) {
            return fromFloatBuffer(
                    this.yln.composeImages(left.width(), left.height(), toFloatBuffer(left),
                            toFloatBuffer(right), MatrixComposition.HYPOT),
                    left.width(), left.height());
        }

        @Override
        public ImageVal min(ImageVal a, ImageVal b) {
            return fromFloatBuffer(
                    this.yln.composeImages(a.width(), a.height(), toFloatBuffer(a),
                            toFloatBuffer(b), MatrixComposition.MIN),
                    a.width(), a.height());
        }

        @Override
        public ImageVal max(ImageVal a, ImageVal b) {
            return fromFloatBuffer(
                    this.yln.composeImages(a.width(), a.height(), toFloatBuffer(a),
                            toFloatBuffer(b), MatrixComposition.MAX),
                    a.width(), a.height());
        }
    }
}
